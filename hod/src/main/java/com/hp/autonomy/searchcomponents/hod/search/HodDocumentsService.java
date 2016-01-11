/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.google.common.collect.ImmutableSet;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Highlight;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Sort;
import com.hp.autonomy.hod.client.api.textindex.query.search.Summary;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationCapable;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@ConditionalOnMissingBean(DocumentsService.class)
public class HodDocumentsService implements DocumentsService<ResourceIdentifier, HodSearchResult, HodErrorException> {
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

    private static final int MAX_SIMILAR_DOCUMENTS = 3;

    private final FindSimilarService<HodSearchResult> findSimilarService;
    private final ConfigService<? extends QueryManipulationCapable> configService;
    private final QueryTextIndexService<HodSearchResult> queryTextIndexService;

    @Autowired
    public HodDocumentsService(final FindSimilarService<HodSearchResult> findSimilarService, final ConfigService<? extends QueryManipulationCapable> configService, final QueryTextIndexService<HodSearchResult> queryTextIndexService) {
        this.findSimilarService = findSimilarService;
        this.configService = configService;
        this.queryTextIndexService = queryTextIndexService;
    }

    @Override
    public Documents<HodSearchResult> queryTextIndex(final SearchRequest<ResourceIdentifier> findQueryParams) throws HodErrorException {
        return queryTextIndex(findQueryParams, false);
    }

    @Override
    public Documents<HodSearchResult> queryTextIndexForPromotions(final SearchRequest<ResourceIdentifier> findQueryParams) throws HodErrorException {
        return queryTextIndex(findQueryParams, true);
    }

    @Override
    public List<HodSearchResult> findSimilar(final Set<ResourceIdentifier> indexes, final String reference) throws HodErrorException {
        final QueryRequestBuilder requestBuilder = new QueryRequestBuilder()
                .setIndexes(indexes)
                .setPrint(Print.none)
                .setAbsoluteMaxResults(MAX_SIMILAR_DOCUMENTS)
                .setSummary(Summary.concept);

        final Documents<HodSearchResult> result = findSimilarService.findSimilarDocumentsToIndexReference(reference, requestBuilder);
        final List<HodSearchResult> documents = new LinkedList<>();

        for (final HodSearchResult document : result.getDocuments()) {
            documents.add(addDomain(indexes, document));
        }

        return documents;
    }

    private Documents<HodSearchResult> queryTextIndex(final SearchRequest<ResourceIdentifier> findQueryParams, final boolean fetchPromotions) throws HodErrorException {
        final String profileName = configService.getConfig().getQueryManipulation().getProfile();

        final QueryRequestBuilder params = new QueryRequestBuilder()
                .setAbsoluteMaxResults(findQueryParams.getMaxResults())
                .setSummary(findQueryParams.getSummary() != null ? Summary.valueOf(findQueryParams.getSummary()) : null)
                .setIndexes(findQueryParams.getIndex())
                .setFieldText(findQueryParams.getFieldText())
                .setQueryProfile(new ResourceIdentifier(getDomain(), profileName))
                .setSort(findQueryParams.getSort() != null ? Sort.valueOf(findQueryParams.getSort()) : null)
                .setMinDate(findQueryParams.getMinDate())
                .setMaxDate(findQueryParams.getMaxDate())
                .setPromotions(fetchPromotions)
                .setPrint(Print.fields)
                .setPrintFields(new ArrayList<>(SearchResult.ALL_FIELDS))
                .setHighlight(Highlight.terms)
                .setStartTag(HIGHLIGHT_START_TAG)
                .setEndTag(HIGHLIGHT_END_TAG);

        final Documents<HodSearchResult> hodDocuments = queryTextIndexService.queryTextIndexWithText(findQueryParams.getQueryText(), params);
        final List<HodSearchResult> documentList = new LinkedList<>();

        for (final HodSearchResult hodSearchResult : hodDocuments.getDocuments()) {
            documentList.add(addDomain(findQueryParams.getIndex(), hodSearchResult));
        }

        return new Documents<>(documentList, hodDocuments.getTotalResults(), hodDocuments.getExpandedQuery());
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
