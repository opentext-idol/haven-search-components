/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.google.common.collect.ImmutableSet;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.CheckSpelling;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Highlight;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Sort;
import com.hp.autonomy.hod.client.api.textindex.query.search.Summary;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationCapable;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HodDocumentsService implements DocumentsService<ResourceIdentifier, HodSearchResult, HodErrorException> {
    // IOD limits max results to 2500
    private static final int IOD_MAX_RESULTS = 2500;

    private static final ImmutableSet<String> PUBLIC_INDEX_NAMES = ImmutableSet.of(
            ResourceIdentifier.WIKI_CHI.getName(),
            ResourceIdentifier.WIKI_ENG.getName(),
            ResourceIdentifier.WIKI_FRA.getName(),
            ResourceIdentifier.WIKI_GER.getName(),
            ResourceIdentifier.WIKI_ITA.getName(),
            ResourceIdentifier.WIKI_SPA.getName(),
            ResourceIdentifier.WORLD_FACTBOOK.getName(),
            ResourceIdentifier.NEWS_ENG.getName(),
            ResourceIdentifier.NEWS_FRA.getName(),
            ResourceIdentifier.NEWS_GER.getName(),
            ResourceIdentifier.NEWS_ITA.getName(),
            ResourceIdentifier.ARXIV.getName(),
            ResourceIdentifier.PATENTS.getName()
    );

    private final FindSimilarService<HodSearchResult> findSimilarService;
    private final ConfigService<? extends QueryManipulationCapable> configService;
    private final QueryTextIndexService<HodSearchResult> queryTextIndexService;

    public HodDocumentsService(final FindSimilarService<HodSearchResult> findSimilarService, final ConfigService<? extends QueryManipulationCapable> configService, final QueryTextIndexService<HodSearchResult> queryTextIndexService) {
        this.findSimilarService = findSimilarService;
        this.configService = configService;
        this.queryTextIndexService = queryTextIndexService;
    }

    @Override
    public Documents<HodSearchResult> queryTextIndex(final SearchRequest<ResourceIdentifier> findQueryParams) throws HodErrorException {
        return queryTextIndex(findQueryParams, findQueryParams.getQueryType() == SearchRequest.QueryType.PROMOTIONS);
    }

    @Override
    public Documents<HodSearchResult> queryTextIndexForPromotions(final SearchRequest<ResourceIdentifier> findQueryParams) throws HodErrorException {
        return queryTextIndex(findQueryParams, true);
    }

    @Override
    public Documents<HodSearchResult> findSimilar(final SuggestRequest<ResourceIdentifier> suggestRequest) throws HodErrorException {
        final QueryRequestBuilder requestBuilder = setQueryParams(suggestRequest);

        return findSimilarService.findSimilarDocumentsToIndexReference(suggestRequest.getReference(), requestBuilder);
    }

    private Documents<HodSearchResult> queryTextIndex(final SearchRequest<ResourceIdentifier> searchRequest, final boolean fetchPromotions) throws HodErrorException {
        final QueryRequestBuilder params = setQueryParams(searchRequest);

        if (searchRequest.isAutoCorrect()) {
            params.setCheckSpelling(CheckSpelling.autocorrect);
        }

        if (fetchPromotions) {
            params.setPromotions(true);
        }

        final Documents<HodSearchResult> hodDocuments = queryTextIndexService.queryTextIndexWithText(searchRequest.getQueryRestrictions().getQueryText(), params);
        final List<HodSearchResult> documentList = new LinkedList<>();

        for (final HodSearchResult hodSearchResult : hodDocuments.getDocuments()) {
            documentList.add(addDomain(searchRequest.getQueryRestrictions().getDatabases(), hodSearchResult));
        }

        return new Documents<>(documentList, hodDocuments.getTotalResults(), hodDocuments.getExpandedQuery(), null, hodDocuments.getAutoCorrection());
    }

    private QueryRequestBuilder setQueryParams(final AciSearchRequest<ResourceIdentifier> searchRequest) {
        final String profileName = configService.getConfig().getQueryManipulation().getProfile();

        final QueryRequestBuilder queryRequestBuilder = new QueryRequestBuilder()
                .setAbsoluteMaxResults(Math.min(searchRequest.getMaxResults(), IOD_MAX_RESULTS))
                .setSummary(searchRequest.getSummary() != null ? Summary.valueOf(searchRequest.getSummary()) : null)
                .setStart(searchRequest.getStart())
                .setMaxPageResults(searchRequest.getMaxResults() - searchRequest.getStart() + 1)
                .setTotalResults(true)
                .setIndexes(searchRequest.getQueryRestrictions().getDatabases())
                .setFieldText(searchRequest.getQueryRestrictions().getFieldText())
                .setQueryProfile(new ResourceIdentifier(getDomain(), profileName))
                .setSort(searchRequest.getSort() != null ? Sort.valueOf(searchRequest.getSort()) : null)
                .setMinDate(searchRequest.getQueryRestrictions().getMinDate())
                .setMaxDate(searchRequest.getQueryRestrictions().getMaxDate())
                .setPrint(Print.fields)
                .setPrintFields(new ArrayList<>(SearchResult.ALL_FIELDS));

        if (searchRequest.isHighlight()) {
            queryRequestBuilder
                    .setHighlight(Highlight.terms)
                    .setStartTag(HIGHLIGHT_START_TAG)
                    .setEndTag(HIGHLIGHT_END_TAG);
        }

        return queryRequestBuilder;
    }

    // Add a domain to a FindDocument, given the collection of indexes which were queried against to return it from HOD
    private HodSearchResult addDomain(final Iterable<ResourceIdentifier> indexIdentifiers, final HodSearchResult document) {
        // HOD does not return the domain for documents yet, but it does return the index
        final String index = document.getIndex();
        String domain = null;

        // It's most likely that the returned documents will be in one of the indexes we are querying (hopefully the
        // names are unique between the domains...)
        for (final ResourceIdentifier indexIdentifier : indexIdentifiers) {
            if (index.equals(indexIdentifier.getName())) {
                domain = indexIdentifier.getDomain();
                break;
            }
        }

        if (domain == null) {
            // If not, it might be a public index
            domain = PUBLIC_INDEX_NAMES.contains(index) ? ResourceIdentifier.PUBLIC_INDEXES_DOMAIN : getDomain();
        }

        return new HodSearchResult.Builder(document)
                .setDomain(domain)
                .build();
    }

    private String getDomain() {
        return ((HodAuthentication) SecurityContextHolder.getContext().getAuthentication()).getPrincipal().getApplication().getDomain();
    }
}
