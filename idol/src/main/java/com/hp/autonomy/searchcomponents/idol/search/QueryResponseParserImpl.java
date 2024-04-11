/*
 * Copyright 2015-2017 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.searchcomponents.core.search.AutoCorrectException;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesService;
import com.hp.autonomy.searchcomponents.idol.search.fields.FieldsParser;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.ExpansionRule;
import com.hp.autonomy.types.requests.Spelling;
import com.hp.autonomy.types.requests.Warnings;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.opentext.idol.types.responses.Database;
import com.opentext.idol.types.responses.Hit;
import com.opentext.idol.types.responses.QueryResponseData;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.idol.search.QueryResponseParser.QUERY_RESPONSE_PARSER_BEAN_NAME;

/**
 * Default implementation of {@link QueryResponseParser}
 */
@SuppressWarnings("WeakerAccess")
@Component(QUERY_RESPONSE_PARSER_BEAN_NAME)
class QueryResponseParserImpl implements QueryResponseParser {
    static final String MISSING_DATABASE_WARNING = "At least one of the databases provided in the query does not exist";
    private static final Pattern SPELLING_SEPARATOR_PATTERN = Pattern.compile(", ");
    private final FieldsParser fieldsParser;
    private final IdolDatabasesService databasesService;
    private final ObjectFactory<IdolDatabasesRequestBuilder> databasesRequestBuilderFactory;

    @Autowired
    QueryResponseParserImpl(final FieldsParser fieldsParser,
                            final IdolDatabasesService databasesService,
                            final ObjectFactory<IdolDatabasesRequestBuilder> databasesRequestBuilderFactory) {
        this.fieldsParser = fieldsParser;
        this.databasesService = databasesService;
        this.databasesRequestBuilderFactory = databasesRequestBuilderFactory;
    }

    @Override
    public Documents<IdolSearchResult> parseQueryResults(final IdolSearchRequest searchRequest, final ActionParameters aciParameters, final QueryResponseData responseData, final Function<ActionParameters, QueryResponseData> queryExecutor) {
        final List<Hit> hits = responseData.getHits();

        final Warnings warnings = parseWarnings(searchRequest, aciParameters, responseData);

        final String spellingQuery = responseData.getSpellingquery();

        // If IDOL has a spelling suggestion, retry query for auto correct
        final Documents<IdolSearchResult> documents;
        if (spellingQuery != null) {
            documents = rerunQueryWithAdjustedSpelling(
                aciParameters, responseData, spellingQuery, warnings, queryExecutor,
                searchRequest.getReferenceField());
        } else {
            final List<IdolSearchResult> results =
                parseQueryHits(hits, searchRequest.getReferenceField());

            final List<ExpansionRule> expansions = Optional.ofNullable(responseData.getExpansionOrder()).map(order ->
                order.getRule().stream()
                    .filter(rule -> {
                        final String ruleType = rule.getRuleType();
                        return "synonym".equals(ruleType) || "blacklist".equals(ruleType);
                    })
                    .map(rule -> new ExpansionRule(rule.getReference(), rule.getRuleType())
                ).collect(Collectors.toList())
            ).orElse(null);

            documents = new Documents<>(results, responseData.getTotalhits(), responseData.getExpandedQuery(), null, null, warnings, expansions);
        }

        return documents;
    }

    protected Warnings parseWarnings(final IdolSearchRequest searchRequest, final ActionParameters aciParameters, final QueryResponseData responseData) {
        Warnings warnings = null;
        for (final String warning : responseData.getWarning()) {
            if (MISSING_DATABASE_WARNING.equals(warning.trim())) {
                final Set<Database> updatedDatabases = databasesService.getDatabases(databasesRequestBuilderFactory.getObject().build());
                final List<String> oldQueryRestrictionDatabases = searchRequest.getQueryRestrictions().getDatabases();
                final Set<String> badDatabases = new LinkedHashSet<>(oldQueryRestrictionDatabases);
                for (final Database database : updatedDatabases) {
                    final String databaseName = database.getName();
                    badDatabases.remove(databaseName);
                }

                warnings = new Warnings(badDatabases);

                final Set<String> newQueryRestrictionDatabases = new LinkedHashSet<>(oldQueryRestrictionDatabases);
                newQueryRestrictionDatabases.removeAll(badDatabases);
                aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(newQueryRestrictionDatabases));
            }
        }
        return warnings;
    }

    protected Documents<IdolSearchResult> rerunQueryWithAdjustedSpelling(
        final ActionParameters aciParameters,
        final QueryResponseData responseData,
        final String spellingQuery,
        final Warnings warnings,
        final Function<ActionParameters, QueryResponseData> queryExecutor,
        final String referenceField
    ) {
        final String originalQuery = (String) aciParameters.get(QueryParams.Text.name());
        aciParameters.put(QueryParams.Text.name(), spellingQuery);

        final Spelling spelling = new Spelling(Arrays.asList(SPELLING_SEPARATOR_PATTERN.split(responseData.getSpelling())), spellingQuery, originalQuery);

        try {
            final QueryResponseData correctedResponseData = queryExecutor.apply(aciParameters);
            final List<IdolSearchResult> correctedResults =
                parseQueryHits(correctedResponseData.getHits(), referenceField);

            return new Documents<>(correctedResults, correctedResponseData.getTotalhits(), null, null, spelling, warnings);
        } catch (final AciErrorException e) {
            throw new AutoCorrectException(e.getMessage(), e, spelling);
        }
    }

    @Override
    public List<IdolSearchResult> parseQueryHits(
        final Collection<Hit> hits, final String referenceField
    ) {
        final List<IdolSearchResult> results = new ArrayList<>(hits.size());
        for (final Hit hit : hits) {
            final IdolSearchResult.IdolSearchResultBuilder searchResultBuilder = new IdolSearchResult.IdolSearchResultBuilder()
                    .reference(hit.getReference())
                    .index(hit.getDatabase())
                    .title(hit.getTitle())
                    .summary(hit.getSummary())
                    .date(hit.getDatestring())
                    .weight(hit.getWeight())
                    .intentRankedHit(hit.getIntentrankedhit())
                    .promotionName(hit.getPromotionname());

            fieldsParser.parseDocumentFields(hit, searchResultBuilder);
            final IdolSearchResult searchResult = searchResultBuilder.build();

            if (referenceField != null && searchResult.getFieldMap().containsKey(referenceField)) {
                results.add(searchResult.toBuilder()
                    .reference((String) searchResult.getFieldMap()
                        .get(referenceField).getValues().get(0).getValue())
                    .build()
                );
            } else {
                results.add(searchResult);
            }
        }
        return results;
    }
}
