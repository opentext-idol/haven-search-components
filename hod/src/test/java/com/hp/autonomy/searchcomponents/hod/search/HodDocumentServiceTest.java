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
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryResults;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Sort;
import com.hp.autonomy.hod.client.api.textindex.query.search.Summary;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.warning.HodWarning;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationConfig;
import com.hp.autonomy.searchcomponents.hod.test.HodTestUtils;
import com.hp.autonomy.types.requests.Documents;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.apache.commons.lang.NotImplementedException;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

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

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.class)
public class HodDocumentServiceTest {
    @Mock
    private FindSimilarService<HodSearchResult> findSimilarService;

    @Mock
    private ConfigService<HodSearchCapable> configService;

    @Mock
    private HodSearchCapable config;

    @Mock
    private QueryTextIndexService<HodSearchResult> queryTextIndexService;

    @Mock
    private GetContentService<HodSearchResult> getContentService;

    @Mock
    private AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;

    @Mock
    private DocumentFieldsService documentFieldsService;

    @Mock
    private HodAuthenticationPrincipal hodAuthenticationPrincipal;

    private HodDocumentsService documentsService;
    private final TestUtils<ResourceIdentifier> testUtils = new HodTestUtils();

    @Before
    public void setUp() {
        documentsService = new HodDocumentsServiceImpl(findSimilarService, configService, queryTextIndexService, getContentService, authenticationInformationRetriever, documentFieldsService);

        when(config.getQueryManipulation()).thenReturn(QueryManipulationConfig.builder().profile("SomeProfile").index("SomeIndex").build());
        when(configService.getConfig()).thenReturn(config);

        when(hodAuthenticationPrincipal.getApplication()).thenReturn(new ResourceIdentifier("SomeDomain", "SomeIndex"));
        when(authenticationInformationRetriever.getPrincipal()).thenReturn(hodAuthenticationPrincipal);
    }

    @Test
    public void queryTextIndexModified() throws HodErrorException {
        final QueryResults<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), any(QueryRequestBuilder.class))).thenReturn(mockedResults);

        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final QueryRequest<ResourceIdentifier> queryRequest = QueryRequest.<ResourceIdentifier>builder()
                .queryRestrictions(queryRestrictions)
                .start(1)
                .maxResults(30)
                .summary(Summary.concept.name())
                .summaryCharacters(250)
                .sort(Sort.relevance.name())
                .highlight(true)
                .autoCorrect(false)
                .print(Print.fields.name())
                .queryType(QueryRequest.QueryType.MODIFIED)
                .build();
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(queryRequest);
        validateResults(results);
    }

    @Test
    public void queryTextIndexRaw() throws HodErrorException {
        final QueryResults<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), argThat(new HasPropertyWithValue<>("queryProfile", nullValue())))).thenReturn(mockedResults);

        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final QueryRequest<ResourceIdentifier> queryRequest = QueryRequest.<ResourceIdentifier>builder()
                .queryRestrictions(queryRestrictions)
                .start(1)
                .maxResults(30)
                .summary(Summary.concept.name())
                .summaryCharacters(250)
                .sort(Sort.relevance.name())
                .highlight(true)
                .autoCorrect(false)
                .print(Print.fields.name())
                .queryType(QueryRequest.QueryType.RAW)
                .build();
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(queryRequest);
        validateResults(results);
    }

    @Test
    public void queryTextIndexForPromotions() throws HodErrorException {
        final QueryResults<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), argThat(new HasPropertyWithValue<>("promotions", is(true))))).thenReturn(mockedResults);

        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final QueryRequest<ResourceIdentifier> queryRequest = QueryRequest.<ResourceIdentifier>builder()
                .queryRestrictions(queryRestrictions)
                .start(1)
                .maxResults(30)
                .summary(Summary.concept.name())
                .summaryCharacters(250)
                .sort(Sort.relevance.name())
                .highlight(true)
                .autoCorrect(true)
                .print(Print.fields.name())
                .queryType(QueryRequest.QueryType.PROMOTIONS)
                .build();
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(queryRequest);
        validateResults(results);
    }

    @Test
    public void findSimilar() throws HodErrorException {
        final QueryRestrictions<ResourceIdentifier> queryRestrictions = testUtils.buildQueryRestrictions();
        final SuggestRequest<ResourceIdentifier> suggestRequest = SuggestRequest.<ResourceIdentifier>builder()
                .reference("SomeReference")
                .queryRestrictions(queryRestrictions)
                .start(1)
                .maxResults(30)
                .summary(Summary.concept.name())
                .summaryCharacters(250)
                .sort(Sort.relevance.name())
                .highlight(true)
                .print(Print.fields.name())
                .build();
        when(findSimilarService.findSimilarDocumentsToIndexReference(anyString(), any(QueryRequestBuilder.class))).thenReturn(mockResults());
        final Documents<HodSearchResult> results = documentsService.findSimilar(suggestRequest);
        validateResults(results);
    }

    @Test
    public void getDocumentContent() throws HodErrorException {
        final GetContentRequestIndex<ResourceIdentifier> getContentRequestIndex = new GetContentRequestIndex<>(new ResourceIdentifier("x", "y"), Collections.singleton("z"));
        final GetContentRequestIndex<ResourceIdentifier> getContentRequestIndex2 = new GetContentRequestIndex<>(new ResourceIdentifier("a", "b"), Collections.singleton("c"));
        when(getContentService.getContent(anyListOf(String.class), any(ResourceIdentifier.class), any(GetContentRequestBuilder.class))).thenReturn(mockResults());
        documentsService.getDocumentContent(GetContentRequest.<ResourceIdentifier>builder().indexesAndReferences(Arrays.asList(getContentRequestIndex, getContentRequestIndex2)).print(Print.fields.name()).build());
        verify(getContentService, times(2)).getContent(anyListOf(String.class), any(ResourceIdentifier.class), any(GetContentRequestBuilder.class));
    }

    @Test(expected = NotImplementedException.class)
    public void getStateToken() throws HodErrorException {
        documentsService.getStateToken(testUtils.buildQueryRestrictions(), 30, false);
    }

    @Test(expected = NotImplementedException.class)
    public void getStateTokenAndResultCount() throws HodErrorException {
        documentsService.getStateTokenAndResultCount(testUtils.buildQueryRestrictions(), 30, false);
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
    private QueryResults<HodSearchResult> mockResults() {
        final HodSearchResult resultWithIndexInQuery = HodSearchResult.builder()
                .index(testUtils.getDatabases().get(0).getName())
                .build();
        final HodSearchResult resultWithPublicIndex = HodSearchResult.builder()
                .index(ResourceIdentifier.NEWS_ENG.getName())
                .build();
        final HodSearchResult resultWithPrivateIndex = HodSearchResult.builder()
                .index("SomeIndex")
                .build();
        final HodWarning warning = new HodWarning.Builder()
                .setCode(40003)
                .setDetails("There is an error setting the promotions. File is missing.")
                .build();
        return new QueryResults<>(Arrays.asList(resultWithIndexInQuery, resultWithPublicIndex, resultWithPrivateIndex), 3, null, null, null, Collections.singletonList(warning));
    }
}
