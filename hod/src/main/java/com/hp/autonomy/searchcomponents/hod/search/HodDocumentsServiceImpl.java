/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.google.common.collect.ImmutableSet;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.caching.CachingConfiguration;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.search.*;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.warning.HodWarning;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.types.requests.Documents;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.hp.autonomy.searchcomponents.core.search.DocumentsService.DOCUMENTS_SERVICE_BEAN_NAME;

/**
 * Default Hod implementation of {@link DocumentsService}
 */
@Slf4j
@Service(DOCUMENTS_SERVICE_BEAN_NAME)
class HodDocumentsServiceImpl implements HodDocumentsService {
    private static final ImmutableSet<String> PUBLIC_INDEX_NAMES = ImmutableSet.of(
            ResourceName.WIKI_CHI.getName(),
            ResourceName.WIKI_ENG.getName(),
            ResourceName.WIKI_FRA.getName(),
            ResourceName.WIKI_GER.getName(),
            ResourceName.WIKI_ITA.getName(),
            ResourceName.WIKI_SPA.getName(),
            ResourceName.WORLD_FACTBOOK.getName(),
            ResourceName.NEWS_ENG.getName(),
            ResourceName.NEWS_FRA.getName(),
            ResourceName.NEWS_GER.getName(),
            ResourceName.NEWS_ITA.getName(),
            ResourceName.ARXIV.getName(),
            ResourceName.PATENTS.getName()
    );

    private final FindSimilarService<HodSearchResult> findSimilarService;
    private final ConfigService<? extends HodSearchCapable> configService;
    private final QueryTextIndexService<HodSearchResult> queryTextIndexService;
    private final GetContentService<HodSearchResult> getContentService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationRetriever;
    private final DocumentFieldsService documentFieldsService;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    @Autowired
    HodDocumentsServiceImpl(
            final FindSimilarService<HodSearchResult> findSimilarService,
            final ConfigService<? extends HodSearchCapable> configService,
            final QueryTextIndexService<HodSearchResult> queryTextIndexService,
            final GetContentService<HodSearchResult> getContentService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationRetriever,
            final DocumentFieldsService documentFieldsService
    ) {
        this.findSimilarService = findSimilarService;
        this.configService = configService;
        this.queryTextIndexService = queryTextIndexService;
        this.getContentService = getContentService;
        this.authenticationRetriever = authenticationRetriever;
        this.documentFieldsService = documentFieldsService;
    }

    @Override
    public Documents<HodSearchResult> queryTextIndex(final HodQueryRequest queryRequest) throws HodErrorException {
        final QueryRequestBuilder params = setQueryParams(queryRequest, queryRequest.getQueryType() != QueryRequest.QueryType.RAW);

        if (queryRequest.isAutoCorrect()) {
            params.setCheckSpelling(CheckSpelling.autocorrect);
        }

        if (queryRequest.getQueryType() == QueryRequest.QueryType.PROMOTIONS) {
            params.setPromotions(true);
            //TODO remove this when IOD have fixed the the default value of the indexes parameter (IOD-6168)
            params.setIndexes(Collections.singletonList(ResourceName.WIKI_ENG));
        }

        final QueryResults<HodSearchResult> hodDocuments = queryTextIndexService.queryTextIndexWithText(queryRequest.getQueryRestrictions().getQueryText(), params);

        checkForWarnings(hodDocuments);

        final List<HodSearchResult> documentList = new LinkedList<>();
        addDomainToSearchResults(documentList, queryRequest.getQueryRestrictions().getDatabases(), hodDocuments.getDocuments());

        final Integer totalResults = hodDocuments.getTotalResults() != null ? hodDocuments.getTotalResults() : 0;
        return new Documents<>(documentList, totalResults, hodDocuments.getExpandedQuery(), null, hodDocuments.getAutoCorrection(), null);
    }

    @Override
    public Documents<HodSearchResult> findSimilar(final HodSuggestRequest suggestRequest) throws HodErrorException {
        final QueryRequestBuilder requestBuilder = setQueryParams(suggestRequest, false);

        final QueryResults<HodSearchResult> results = findSimilarService.findSimilarDocumentsToIndexReference(suggestRequest.getReference(), requestBuilder);

        checkForWarnings(results);

        final List<HodSearchResult> documentList = new LinkedList<>();
        addDomainToSearchResults(documentList, suggestRequest.getQueryRestrictions().getDatabases(), results.getDocuments());

        return new Documents<>(documentList, results.getTotalResults(), results.getExpandedQuery(), results.getSuggestion(), results.getAutoCorrection(), null);
    }

    private void checkForWarnings(final QueryResults<HodSearchResult> results) {
        final List<HodWarning> warnings = results.getHodWarnings();
        if (!warnings.isEmpty()) {
            for (final HodWarning warning : warnings) {
                log.warn("HoD returned a warning of type " + warning);
            }
        }
    }

    @Cacheable(value = CacheNames.GET_DOCUMENT_CONTENT, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    @Override
    public List<HodSearchResult> getDocumentContent(final HodGetContentRequest request) throws HodErrorException {
        final List<HodSearchResult> contentResults = new ArrayList<>();

        for (final GetContentRequestIndex<ResourceName> indexAndReferences : request.getIndexesAndReferences()) {
            final GetContentRequestBuilder builder = new GetContentRequestBuilder()
                    .setPrintFields(documentFieldsService.getPrintFields(Collections.emptyList()))
                    .setSummary(Summary.concept)
                    .setSecurityInfo(authenticationRetriever.getPrincipal().getSecurityInfo());

            final List<HodSearchResult> documents = getContentService.getContent(new ArrayList<>(indexAndReferences.getReferences()), indexAndReferences.getIndex(), builder).getDocuments();
            addDomainToSearchResults(contentResults, Collections.singleton(indexAndReferences.getIndex()), documents);
        }

        return contentResults;
    }

    @Override
    public String getStateToken(final HodQueryRestrictions queryRestrictions, final int maxResults, final boolean promotions) throws HodErrorException {
        throw new NotImplementedException("State tokens are not yet retrievable from Haven OnDemand");
    }

    @Override
    public StateTokenAndResultCount getStateTokenAndResultCount(final HodQueryRestrictions queryRestrictions, final int maxResults, final boolean promotions) throws HodErrorException {
        throw new NotImplementedException("State tokens are not yet retrievable from Haven OnDemand");
    }

    private QueryRequestBuilder setQueryParams(final HodSearchRequest searchRequest, final boolean setQueryProfile) {
        final String profileName = configService.getConfig().getQueryManipulation().getProfile();

        final Print print = Optional.ofNullable(searchRequest.getPrint()).map(Print::valueOf).orElse(null);
        final QueryRequestBuilder queryRequestBuilder = new QueryRequestBuilder()
                .setAbsoluteMaxResults(Math.min(searchRequest.getMaxResults(), HOD_MAX_RESULTS))
                .setSummary(Optional.ofNullable(searchRequest.getSummary()).map(Summary::valueOf).orElse(null))
                .setStart(searchRequest.getStart())
                .setMaxPageResults(searchRequest.getMaxResults() - searchRequest.getStart() + 1)
                .setTotalResults(true)
                .setIndexes(searchRequest.getQueryRestrictions().getDatabases())
                .setFieldText(searchRequest.getQueryRestrictions().getFieldText())
                .setSort(Optional.ofNullable(searchRequest.getSort()).map(Sort::valueOf).orElse(null))
                .setMinDate(searchRequest.getQueryRestrictions().getMinDate())
                .setMaxDate(searchRequest.getQueryRestrictions().getMaxDate())
                .setPrint(print)
                .setMinScore(searchRequest.getQueryRestrictions().getMinScore())
                .setSecurityInfo(authenticationRetriever.getPrincipal().getSecurityInfo());

        if (print == Print.fields) {
            queryRequestBuilder.setPrintFields(documentFieldsService.getPrintFields(searchRequest.getPrintFields()));
        }

        if (searchRequest.isHighlight()) {
            queryRequestBuilder
                    .setHighlight(Highlight.terms)
                    .setStartTag(HIGHLIGHT_START_TAG)
                    .setEndTag(HIGHLIGHT_END_TAG);
        }

        if (setQueryProfile) {
            queryRequestBuilder.setQueryProfile(new ResourceName(getDomain(), profileName));
        }

        return queryRequestBuilder;
    }

    private void addDomainToSearchResults(final Collection<HodSearchResult> documentList, final Iterable<ResourceName> indexIdentifiers, final Iterable<HodSearchResult> documents) {
        for (final HodSearchResult hodSearchResult : documents) {
            documentList.add(addDomain(indexIdentifiers, hodSearchResult));
        }
    }

    // Add a domain to a FindDocument, given the collection of indexes which were queried against to return it from HOD
    private HodSearchResult addDomain(final Iterable<ResourceName> indexIdentifiers, final HodSearchResult document) {
        // HOD does not return the domain for documents yet, but it does return the index
        final String index = document.getIndex();
        String domain = null;

        // It's most likely that the returned documents will be in one of the indexes we are querying (hopefully the
        // names are unique between the domains...)
        for (final ResourceName indexIdentifier : indexIdentifiers) {
            if (index.equals(indexIdentifier.getName())) {
                domain = indexIdentifier.getDomain();
                break;
            }
        }

        if (domain == null) {
            // If not, it might be a public index
            domain = PUBLIC_INDEX_NAMES.contains(index) ? ResourceName.PUBLIC_INDEXES_DOMAIN : getDomain();
        }

        return document.toBuilder()
                .domain(domain)
                .build();
    }

    private String getDomain() {
        return authenticationRetriever.getPrincipal().getApplication().getDomain();
    }
}
