/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.AutoCorrectException;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequest;
import com.hp.autonomy.searchcomponents.idol.search.fields.FieldsParser;
import com.hp.autonomy.types.idol.Database;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.Spelling;
import com.hp.autonomy.types.requests.Warnings;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Default implementation of Idol QueryResponseParser
 */
@SuppressWarnings("WeakerAccess")
@Component
public class QueryResponseParserImpl implements QueryResponseParser {
    private static final Pattern SPELLING_SEPARATOR_PATTERN = Pattern.compile(", ");

    static final String MISSING_DATABASE_WARNING = "At least one of the databases provided in the query does not exist";

    private final FieldsParser fieldsParser;
    private final DatabasesService<Database, IdolDatabasesRequest, AciErrorException> databasesService;

    @Autowired
    public QueryResponseParserImpl(final FieldsParser fieldsParser, final DatabasesService<Database, IdolDatabasesRequest, AciErrorException> databasesService) {
        this.fieldsParser = fieldsParser;
        this.databasesService = databasesService;
    }

    @Override
    public Documents<IdolSearchResult> parseQueryResults(final AciSearchRequest<String> searchRequest, final AciParameters aciParameters, final QueryResponseData responseData, final IdolDocumentService.QueryExecutor queryExecutor) {
        final List<Hit> hits = responseData.getHits();

        final Warnings warnings = parseWarnings(searchRequest, aciParameters, responseData);

        final String spellingQuery = responseData.getSpellingquery();

        // If IDOL has a spelling suggestion, retry query for auto correct
        final Documents<IdolSearchResult> documents;
        if (spellingQuery != null) {
            documents = rerunQueryWithAdjustedSpelling(aciParameters, responseData, spellingQuery, warnings, queryExecutor);
        } else {
            final List<IdolSearchResult> results = parseQueryHits(hits);
            documents = new Documents<>(results, responseData.getTotalhits(), responseData.getExpandedQuery(), null, null, warnings);
        }

        return documents;
    }

    protected Warnings parseWarnings(final AciSearchRequest<String> searchRequest, final AciParameters aciParameters, final QueryResponseData responseData) {
        Warnings warnings = null;
        for (final String warning : responseData.getWarning()) {
            if (MISSING_DATABASE_WARNING.equals(warning.trim())) {
                final Set<Database> updatedDatabases = databasesService.getDatabases(new IdolDatabasesRequest());
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

    protected Documents<IdolSearchResult> rerunQueryWithAdjustedSpelling(final AciParameters aciParameters, final QueryResponseData responseData, final String spellingQuery, final Warnings warnings, final IdolDocumentService.QueryExecutor queryExecutor) {
        final String originalQuery = aciParameters.get(QueryParams.Text.name());
        aciParameters.put(QueryParams.Text.name(), spellingQuery);

        final Spelling spelling = new Spelling(Arrays.asList(SPELLING_SEPARATOR_PATTERN.split(responseData.getSpelling())), spellingQuery, originalQuery);

        try {
            final QueryResponseData correctedResponseData = queryExecutor.execute(aciParameters);
            final List<IdolSearchResult> correctedResults = parseQueryHits(correctedResponseData.getHits());

            return new Documents<>(correctedResults, correctedResponseData.getTotalhits(), null, null, spelling, warnings);
        } catch (AciErrorException e) {
            throw new AutoCorrectException(e.getMessage(), e, spelling);
        }
    }

    @Override
    public List<IdolSearchResult> parseQueryHits(final Collection<Hit> hits) {
        final List<IdolSearchResult> results = new ArrayList<>(hits.size());
        for (final Hit hit : hits) {
            final IdolSearchResult.Builder searchResultBuilder = new IdolSearchResult.Builder()
                    .setReference(hit.getReference())
                    .setIndex(hit.getDatabase())
                    .setTitle(hit.getTitle())
                    .setSummary(hit.getSummary())
                    .setDate(hit.getDatestring())
                    .setWeight(hit.getWeight())
                    .setPromotionName(hit.getPromotionname());

            fieldsParser.parseDocumentFields(hit, searchResultBuilder);
            results.add(searchResultBuilder.build());
        }
        return results;
    }
}
