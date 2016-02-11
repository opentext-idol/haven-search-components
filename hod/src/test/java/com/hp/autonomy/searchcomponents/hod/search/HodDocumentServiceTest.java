/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationCapable;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationConfig;
import com.hp.autonomy.searchcomponents.hod.test.HodTestUtils;
import com.hp.autonomy.types.requests.Documents;
import org.apache.commons.lang.NotImplementedException;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HodDocumentServiceTest {
    @Mock
    protected FindSimilarService<HodSearchResult> findSimilarService;

    @Mock
    protected ConfigService<? extends QueryManipulationCapable> configService;

    @Mock
    protected QueryManipulationCapable config;

    @Mock
    protected QueryTextIndexService<HodSearchResult> queryTextIndexService;

    @Mock
    protected GetContentService<HodSearchResult> getContentService;

    @Mock
    protected AuthenticationInformationRetriever<HodAuthentication> authenticationInformationRetriever;

    @Mock
    protected HodAuthentication hodAuthentication;

    @Mock
    protected HodAuthenticationPrincipal hodAuthenticationPrincipal;

    protected HodDocumentsService documentsService;
    protected final HodTestUtils testUtils = new HodTestUtils();

    @Before
    public void setUp() {
        documentsService = new HodDocumentsService(findSimilarService, configService, queryTextIndexService, getContentService, authenticationInformationRetriever);

        when(config.getQueryManipulation()).thenReturn(new QueryManipulationConfig("SomeProfile", "SomeIndex"));
        when(configService.getConfig()).thenReturn(config);

        when(hodAuthenticationPrincipal.getApplication()).thenReturn(new ResourceIdentifier("SomeDomain", "SomeIndex"));
        when(hodAuthentication.getPrincipal()).thenReturn(hodAuthenticationPrincipal);
        when(authenticationInformationRetriever.getAuthentication()).thenReturn(hodAuthentication);
    }

    @Test
    public void queryTextIndexModified() throws HodErrorException {
        final Documents<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), any(QueryRequestBuilder.class))).thenReturn(mockedResults);

        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final SearchRequest<ResourceIdentifier> searchRequest = new SearchRequest<>(queryRestrictions, 1, 30, "concept", null, true, false, SearchRequest.QueryType.MODIFIED);
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(searchRequest);
        validateResults(results);
    }

    @Test
    public void queryTextIndexRaw() throws HodErrorException {
        final Documents<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), argThat(new HasPropertyWithValue<QueryRequestBuilder>("queryProfile", nullValue())))).thenReturn(mockedResults);

        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final SearchRequest<ResourceIdentifier> searchRequest = new SearchRequest<>(queryRestrictions, 1, 30, "concept", null, true, false, SearchRequest.QueryType.RAW);
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(searchRequest);
        validateResults(results);
    }

    @Test
    public void queryTextIndexForPromotions() throws HodErrorException {
        final Documents<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), argThat(new HasPropertyWithValue<QueryRequestBuilder>("promotions", is(true))))).thenReturn(mockedResults);

        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final SearchRequest<ResourceIdentifier> searchRequest = new SearchRequest<>(queryRestrictions, 1, 30, "concept", null, true, true, SearchRequest.QueryType.PROMOTIONS);
        final Documents<HodSearchResult> results = documentsService.queryTextIndexForPromotions(searchRequest);
        validateResults(results);
    }

    @Test
    public void findSimilar() throws HodErrorException {
        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final SuggestRequest<ResourceIdentifier> suggestRequest = new SuggestRequest<>("SomeReference", queryRestrictions, 1, 30, "concept", null, true);
        documentsService.findSimilar(suggestRequest);
        verify(findSimilarService).findSimilarDocumentsToIndexReference(anyString(), any(QueryRequestBuilder.class));
    }

    @Test
    public void getDocumentContent() throws HodErrorException {
        final GetContentRequestIndex<ResourceIdentifier> getContentRequestIndex = new GetContentRequestIndex<>(new ResourceIdentifier("x", "y"), Collections.singleton("z"));
        final GetContentRequestIndex<ResourceIdentifier> getContentRequestIndex2 = new GetContentRequestIndex<>(new ResourceIdentifier("a", "b"), Collections.singleton("c"));
        when(getContentService.getContent(anyListOf(String.class), any(ResourceIdentifier.class), any(GetContentRequestBuilder.class))).thenReturn(mockResults());
        documentsService.getDocumentContent(new GetContentRequest<>(new HashSet<>(Arrays.asList(getContentRequestIndex, getContentRequestIndex2))));
        verify(getContentService, times(2)).getContent(anyListOf(String.class), any(ResourceIdentifier.class), any(GetContentRequestBuilder.class));
    }

    @Test(expected = NotImplementedException.class)
    public void getStateToken() throws HodErrorException {
        documentsService.getStateToken(testUtils.buildQueryRestrictions(), 30);
    }

    private void validateResults(final Documents<HodSearchResult> results) {
        assertNotNull(results);
        assertThat(results.getDocuments(), not(empty()));
        assertEquals((long) results.getTotalResults(), results.getDocuments().size());
        for (final HodSearchResult result : results.getDocuments()) {
            assertNotNull(result.getDomain());
        }
    }

    @SuppressWarnings("CastToConcreteClass")
    private Documents<HodSearchResult> mockResults() {
        final HodSearchResult resultWithIndexInQuery = new HodSearchResult.Builder().setIndex(testUtils.getDatabases().get(0).getName()).build();
        final HodSearchResult resultWithPublicIndex = new HodSearchResult.Builder().setIndex(ResourceIdentifier.NEWS_ENG.getName()).build();
        final HodSearchResult resultWithPrivateIndex = new HodSearchResult.Builder().setIndex("SomeIndex").build();
        return new Documents<>(Arrays.asList(resultWithIndexInQuery, resultWithPublicIndex, resultWithPrivateIndex), 3, null, null, null, null);
    }
}
