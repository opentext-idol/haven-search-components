/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryResults;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.warning.HodWarning;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationConfig;
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
import java.util.HashSet;
import java.util.function.BiFunction;

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

    @Mock
    private HodQueryRequest queryRequest;

    @Mock
    private HodSuggestRequest suggestRequest;

    @Mock
    private HodGetContentRequest getContentRequest;

    @Mock
    private HodQueryRestrictions queryRestrictions;

    private HodDocumentsService documentsService;

    @Before
    public void setUp() {
        documentsService = new HodDocumentsServiceImpl(findSimilarService, configService, queryTextIndexService, getContentService, authenticationInformationRetriever, documentFieldsService);

        when(config.getQueryManipulation()).thenReturn(QueryManipulationConfig.builder().profile("SomeProfile").index("SomeIndex").build());
        when(configService.getConfig()).thenReturn(config);

        when(hodAuthenticationPrincipal.getApplication()).thenReturn(new ResourceName("SomeDomain", "SomeIndex"));
        when(authenticationInformationRetriever.getPrincipal()).thenReturn(hodAuthenticationPrincipal);

        when(queryRequest.getQueryRestrictions()).thenReturn(queryRestrictions);
        when(suggestRequest.getQueryRestrictions()).thenReturn(queryRestrictions);
    }

    @Test
    public void queryTextIndexModified() throws HodErrorException {
        final QueryResults<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), any(QueryRequestBuilder.class))).thenReturn(mockedResults);

        when(queryRequest.getQueryType()).thenReturn(QueryRequest.QueryType.MODIFIED);
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(queryRequest);
        validateResults(results);
    }

    @Test
    public void queryTextIndexRaw() throws HodErrorException {
        final QueryResults<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), argThat(new HasPropertyWithValue<>("queryProfile", nullValue())))).thenReturn(mockedResults);

        when(queryRequest.getQueryType()).thenReturn(QueryRequest.QueryType.RAW);
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(queryRequest);
        validateResults(results);
    }

    @Test
    public void queryTextIndexForPromotions() throws HodErrorException {
        final QueryResults<HodSearchResult> mockedResults = mockResults();
        when(queryTextIndexService.queryTextIndexWithText(anyString(), argThat(new HasPropertyWithValue<>("promotions", is(true))))).thenReturn(mockedResults);

        when(queryRequest.getQueryType()).thenReturn(QueryRequest.QueryType.PROMOTIONS);
        final Documents<HodSearchResult> results = documentsService.queryTextIndex(queryRequest);
        validateResults(results);
    }

    @Test
    public void findSimilar() throws HodErrorException {
        when(findSimilarService.findSimilarDocumentsToIndexReference(anyString(), any(QueryRequestBuilder.class))).thenReturn(mockResults());
        final Documents<HodSearchResult> results = documentsService.findSimilar(suggestRequest);
        validateResults(results);
    }

    @Test
    public void getDocumentContent() throws HodErrorException {
        final BiFunction<ResourceName, String, HodGetContentRequestIndex> mockFn = (r, s) -> {
            final HodGetContentRequestIndex getContentRequestIndex = mock(HodGetContentRequestIndex.class);
            when(getContentRequestIndex.getIndex()).thenReturn(r);
            when(getContentRequestIndex.getReferences()).thenReturn(Collections.singleton(s));
            return getContentRequestIndex;
        };
        final HodGetContentRequestIndex getContentRequestIndex = mockFn.apply(new ResourceName("x", "y"), "z");
        final HodGetContentRequestIndex getContentRequestIndex2 = mockFn.apply(new ResourceName("a", "b"), "c");
        when(getContentRequest.getIndexesAndReferences()).thenReturn(new HashSet<>(Arrays.asList(getContentRequestIndex, getContentRequestIndex2)));
        when(getContentService.getContent(anyListOf(String.class), any(ResourceName.class), any(GetContentRequestBuilder.class))).thenReturn(mockResults());
        documentsService.getDocumentContent(getContentRequest);
        verify(getContentService, times(2)).getContent(anyListOf(String.class), any(ResourceName.class), any(GetContentRequestBuilder.class));
    }

    @Test(expected = NotImplementedException.class)
    public void getStateToken() throws HodErrorException {
        documentsService.getStateToken(queryRestrictions, 30, false);
    }

    @Test(expected = NotImplementedException.class)
    public void getStateTokenAndResultCount() throws HodErrorException {
        documentsService.getStateTokenAndResultCount(queryRestrictions, 30, false);
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
                .index(ResourceName.WIKI_ENG.getName())
                .build();
        final HodSearchResult resultWithPublicIndex = HodSearchResult.builder()
                .index(ResourceName.NEWS_ENG.getName())
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
