/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.google.common.collect.ImmutableSet;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.caching.CachingConfiguration;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.search.CheckSpelling;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Highlight;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryResults;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Sort;
import com.hp.autonomy.hod.client.api.textindex.query.search.Summary;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.warning.HodWarning;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.types.requests.Documents;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;

@SuppressWarnings("WeakerAccess")
@Slf4j
public class HodDocumentsService implements DocumentsService<ResourceIdentifier, HodSearchResult, HodErrorException> {
    // IOD limits max results to 2500
    public static final int HOD_MAX_RESULTS = 2500;

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
    private final ConfigService<? extends HodSearchCapable> configService;
    private final QueryTextIndexService<HodSearchResult> queryTextIndexService;
    private final GetContentService<HodSearchResult> getContentService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationRetriever;
    private final DocumentFieldsService documentFieldsService;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public HodDocumentsService(
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
    public Documents<HodSearchResult> queryTextIndex(final SearchRequest<ResourceIdentifier> searchRequest) throws HodErrorException {
        final QueryRequestBuilder params = setQueryParams(searchRequest, searchRequest.getQueryType() != SearchRequest.QueryType.RAW);

        if (searchRequest.isAutoCorrect()) {
            params.setCheckSpelling(CheckSpelling.autocorrect);
        }

        if (searchRequest.getQueryType() == SearchRequest.QueryType.PROMOTIONS) {
            params.setPromotions(true);
            //TODO remove this when IOD have fixed the the default value of the indexes parameter (IOD-6168)
            params.setIndexes(Collections.singletonList(ResourceIdentifier.WIKI_ENG));
        }

        final QueryResults<HodSearchResult> hodDocuments = queryTextIndexService.queryTextIndexWithText(searchRequest.getQueryRestrictions().getQueryText(), params);

        checkForWarnings(hodDocuments);

        final List<HodSearchResult> documentList = new LinkedList<>();
        addDomainToSearchResults(documentList, searchRequest.getQueryRestrictions().getDatabases(), hodDocuments.getDocuments());

        final Integer totalResults = hodDocuments.getTotalResults() != null ? hodDocuments.getTotalResults() : 0;
        return new Documents<>(documentList, totalResults, hodDocuments.getExpandedQuery(), null, hodDocuments.getAutoCorrection(), null);
    }

    @Override
    public Documents<HodSearchResult> findSimilar(final SuggestRequest<ResourceIdentifier> suggestRequest) throws HodErrorException {
        final QueryRequestBuilder requestBuilder = setQueryParams(suggestRequest, false);

        final QueryResults<HodSearchResult> results = findSimilarService.findSimilarDocumentsToIndexReference(suggestRequest.getReference(), requestBuilder);

        checkForWarnings(results);

        final List<HodSearchResult> documentList = new LinkedList<>();
        addDomainToSearchResults(documentList, suggestRequest.getQueryRestrictions().getDatabases(), results.getDocuments());

        return new Documents<>(documentList, results.getTotalResults(), results.getExpandedQuery(), results.getSuggestion(), results.getAutoCorrection(), null);
    }

    private void checkForWarnings(final QueryResults<HodSearchResult> results) {
        final List<HodWarning> warnings = results.getHodWarnings();
        if(!warnings.isEmpty()) {
            for (final HodWarning warning : warnings) {
                log.warn("HoD returned a warning of type " + warning);
            }
        }
    }

    @Cacheable(value = CacheNames.GET_DOCUMENT_CONTENT, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    @Override
    public List<HodSearchResult> getDocumentContent(final GetContentRequest<ResourceIdentifier> request) throws HodErrorException {
        final List<HodSearchResult> contentResults = new ArrayList<>();

        for (final GetContentRequestIndex<ResourceIdentifier> indexAndReferences : request.getIndexesAndReferences()) {
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
    public String getStateToken(final QueryRestrictions<ResourceIdentifier> queryRestrictions, final int maxResults, final boolean promotions) throws HodErrorException {
        throw new NotImplementedException("State tokens are not yet retrievable from Haven OnDemand");
    }

    @Override
    public StateTokenAndResultCount getStateTokenAndResultCount(final QueryRestrictions<ResourceIdentifier> queryRestrictions, final int maxResults, final boolean promotions) throws HodErrorException {
        throw new NotImplementedException("State tokens are not yet retrievable from Haven OnDemand");
    }

    private QueryRequestBuilder setQueryParams(final AciSearchRequest<ResourceIdentifier> searchRequest, final boolean setQueryProfile) {
        final String profileName = configService.getConfig().getQueryManipulation().getProfile();

        final Print print = Print.valueOf(searchRequest.getPrint().toLowerCase());
        final QueryRequestBuilder queryRequestBuilder = new QueryRequestBuilder()
                .setAbsoluteMaxResults(Math.min(searchRequest.getMaxResults(), HOD_MAX_RESULTS))
                .setSummary(searchRequest.getSummary() != null ? Summary.valueOf(searchRequest.getSummary()) : null)
                .setStart(searchRequest.getStart())
                .setMaxPageResults(searchRequest.getMaxResults() - searchRequest.getStart() + 1)
                .setTotalResults(true)
                .setIndexes(searchRequest.getQueryRestrictions().getDatabases())
                .setFieldText(searchRequest.getQueryRestrictions().getFieldText())
                .setSort(searchRequest.getSort() != null ? Sort.valueOf(searchRequest.getSort()) : null)
                .setMinDate(searchRequest.getQueryRestrictions().getMinDate())
                .setMaxDate(searchRequest.getQueryRestrictions().getMaxDate())
                .setPrint(Print.fields)
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
            queryRequestBuilder.setQueryProfile(new ResourceIdentifier(getDomain(), profileName));
        }

        return queryRequestBuilder;
    }

    private void addDomainToSearchResults(final Collection<HodSearchResult> documentList, final Iterable<ResourceIdentifier> indexIdentifiers, final Iterable<HodSearchResult> documents) {
        for (final HodSearchResult hodSearchResult : documents) {
            documentList.add(addDomain(indexIdentifiers, hodSearchResult));
        }
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
        return authenticationRetriever.getPrincipal().getApplication().getDomain();
    }
}
