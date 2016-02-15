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
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequest;
import com.hp.autonomy.types.idol.Database;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.Spelling;
import com.hp.autonomy.types.requests.Warnings;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Default implementation of Idol QueryResponseParser
 */
@Component
public class QueryResponseParserImpl implements QueryResponseParser {
    private static final Pattern SPELLING_SEPARATOR_PATTERN = Pattern.compile(", ");

    static final String MISSING_DATABASE_WARNING = "At least one of the databases provided in the query does not exist";

    private final DatabasesService<Database, IdolDatabasesRequest, AciErrorException> databasesService;

    @Autowired
    public QueryResponseParserImpl(final DatabasesService<Database, IdolDatabasesRequest, AciErrorException> databasesService) {
        this.databasesService = databasesService;
    }

    @Override
    public Documents<IdolSearchResult> parseQueryResults(final AciSearchRequest<String> searchRequest, final AciParameters aciParameters, final QueryResponseData responseData, final IdolDocumentService.QueryExecutor queryExecutor) {
        final List<Hit> hits = responseData.getHit();

        final Warnings warnings = parseWarnings(searchRequest, aciParameters, responseData);

        final String spellingQuery = responseData.getSpellingquery();

        // If IDOL has a spelling suggestion, retry query for auto correct
        final Documents<IdolSearchResult> documents;
        if (spellingQuery != null) {
            documents = rerunQueryWithAdjustedSpelling(aciParameters, responseData, spellingQuery, warnings, queryExecutor);
        } else {
            final List<IdolSearchResult> results = parseQueryHits(hits);
            documents = new Documents<>(results, responseData.getTotalhits(), null, null, null, warnings);
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
        final AciParameters correctedParameters = new AciParameters(aciParameters);
        correctedParameters.put(QueryParams.Text.name(), spellingQuery);

        final QueryResponseData correctedResponseData = queryExecutor.execute(aciParameters);
        final List<IdolSearchResult> correctedResults = parseQueryHits(correctedResponseData.getHit());

        final Spelling spelling = new Spelling(Arrays.asList(SPELLING_SEPARATOR_PATTERN.split(responseData.getSpelling())), spellingQuery, aciParameters.get(QueryParams.Text.name()));

        return new Documents<>(correctedResults, correctedResponseData.getTotalhits(), null, null, spelling, warnings);
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

            final DocContent content = hit.getContent();
            if (content != null) {
                final Element docContent = (Element) content.getContent().get(0);
                searchResultBuilder
                        .setContentType(parseField(docContent, IdolSearchResult.CONTENT_TYPE_FIELD))
                        .setUrl(parseField(docContent, IdolSearchResult.URL_FIELD))
                        .setAuthors(parseFields(docContent, IdolSearchResult.AUTHOR_FIELD))
                        .setCategories(parseFields(docContent, IdolSearchResult.CATEGORY_FIELD))
                        .setDateCreated(parseDateField(docContent, Arrays.asList(IdolSearchResult.DATE_CREATED_FIELD, IdolSearchResult.CREATED_DATE_FIELD)))
                        .setDateModified(parseDateField(docContent, Arrays.asList(IdolSearchResult.MODIFIED_DATE_FIELD, IdolSearchResult.DATE_MODIFIED_FIELD)))
                        .setQmsId(parseField(docContent, IdolSearchResult.QMS_ID_FIELD))
                        .setThumbnail(parseField(docContent, IdolSearchResult.THUMBNAIL))
                        .setMmapUrl(parseField(docContent, IdolSearchResult.MMAP_URL))
                        .setPromotionCategory(determinePromotionCategory(docContent, hit.getPromotionname(), hit.getDatabase()));
            }
            results.add(searchResultBuilder.build());
        }
        return results;
    }

    private PromotionCategory determinePromotionCategory(final Element docContent, final CharSequence promotionName, final CharSequence database) {
        final PromotionCategory promotionCategory;
        if (Boolean.parseBoolean(parseField(docContent, IdolSearchResult.INJECTED_PROMOTION_FIELD))) {
            promotionCategory = PromotionCategory.CARDINAL_PLACEMENT;
        } else if (StringUtils.isNotEmpty(promotionName)) {
            // If the database isn't found, then assume it is a static content promotion
            promotionCategory = StringUtils.isNotEmpty(database) ? PromotionCategory.SPOTLIGHT : PromotionCategory.STATIC_CONTENT_PROMOTION;
        } else {
            promotionCategory = PromotionCategory.NONE;
        }

        return promotionCategory;
    }

    private List<String> parseFields(final Element node, final Iterable<String> fieldNames) {
        final List<String> values = new ArrayList<>();

        final Iterator<String> iterator = fieldNames.iterator();
        while (iterator.hasNext() && values.isEmpty()) {
            values.addAll(parseFields(node, iterator.next()));
        }

        return values;
    }

    private List<String> parseFields(final Element node, final String fieldName) {
        final NodeList childNodes = node.getElementsByTagName(fieldName.toUpperCase());
        final int length = childNodes.getLength();
        final List<String> values = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Node childNode = childNodes.item(i);
            values.add(childNode.getFirstChild().getNodeValue());
        }

        return values;
    }

    private String parseField(final Element node, final Iterable<String> fieldNames) {
        final List<String> fields = parseFields(node, fieldNames);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }

    private String parseField(final Element node, final String fieldName) {
        final List<String> fields = parseFields(node, fieldName);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }

    private DateTime parseDateField(final Element node, final Iterable<String> fieldNames) {
        final String stringField = parseField(node, fieldNames);
        return stringField != null ? DateTime.parse(stringField) : null;
    }
}
