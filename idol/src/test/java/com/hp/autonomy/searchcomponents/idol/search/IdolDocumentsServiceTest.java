/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
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

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.types.idol.responses.Hit;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.idol.responses.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.class)
public class IdolDocumentsServiceTest {
    private static final String MOCK_STATE_TOKEN = "mock-state-token";
    private static final int MOCK_TOTAL_HITS = 42;

    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private QueryResponseParser queryResponseParser;

    @Mock
    private IdolQueryRestrictions queryRestrictions;

    @Mock
    private IdolQueryRequest queryRequest;

    @Mock
    private IdolSuggestRequest suggestRequest;

    @Mock
    private IdolGetContentRequest getContentRequest;

    @Mock
    private IdolGetContentRequestIndex getContentRequestIndex;

    private IdolDocumentsService idolDocumentsService;

    @Before
    public void setUp() {
        idolDocumentsService = new IdolDocumentsServiceImpl(parameterHandler, queryExecutor, queryResponseParser);
    }

    @Test
    public void queryContent() {
        when(queryExecutor.performQuery(any())).thenReturn(true);

        final QueryResponseData responseData = new QueryResponseData();
        when(queryExecutor.executeQuery(any(), any())).thenReturn(responseData);

        idolDocumentsService.queryTextIndex(mockQueryParams(QueryRequest.QueryType.RAW));
        verify(queryResponseParser).parseQueryResults(any(), any(AciParameters.class), eq(responseData), any());
    }

    @Test
    public void queryQms() {
        when(queryExecutor.performQuery(any())).thenReturn(true);

        final QueryResponseData responseData = new QueryResponseData();
        when(queryExecutor.executeQuery(any(), any())).thenReturn(responseData);

        idolDocumentsService.queryTextIndex(mockQueryParams(QueryRequest.QueryType.MODIFIED));
        verify(queryResponseParser).parseQueryResults(any(), any(AciParameters.class), eq(responseData), any());
    }

    @Test
    public void queryContentForPromotions() {
        final Documents<IdolSearchResult> results = idolDocumentsService.queryTextIndex(mockQueryParams(QueryRequest.QueryType.PROMOTIONS));
        assertThat(results.getDocuments(), is(empty()));
    }

    @Test
    public void queryQmsForPromotions() {
        when(queryExecutor.performQuery(any())).thenReturn(true);

        final QueryResponseData responseData = new QueryResponseData();
        when(queryExecutor.executeQuery(any(), any())).thenReturn(responseData);

        idolDocumentsService.queryTextIndex(mockQueryParams(QueryRequest.QueryType.PROMOTIONS));
        verify(queryResponseParser).parseQueryResults(any(), any(AciParameters.class), eq(responseData), any());
    }

    @Test
    public void findSimilar() {
        final SuggestResponseData responseData = new SuggestResponseData();
        responseData.setTotalhits(1);
        final Hit hit = new Hit();
        responseData.getHits().add(hit);

        when(queryExecutor.executeSuggest(any(), any())).thenReturn(responseData);

        when(suggestRequest.getReference()).thenReturn("Some reference");
        when(suggestRequest.getQueryRestrictions()).thenReturn(queryRestrictions);
        idolDocumentsService.findSimilar(suggestRequest);
        verify(queryResponseParser).parseQueryHits(responseData.getHits());
    }

    @Test
    public void getContent() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setTotalhits(1);
        final Hit hit = new Hit();
        responseData.getHits().add(hit);

        when(queryExecutor.executeQuery(any(), any())).thenReturn(responseData);

        when(getContentRequestIndex.getIndex()).thenReturn("Database1");
        when(getContentRequestIndex.getReferences()).thenReturn(Collections.singleton("Some reference"));
        when(getContentRequest.getIndexesAndReferences()).thenReturn(Collections.singleton(getContentRequestIndex));
        when(getContentRequest.getPrint()).thenReturn(PrintParam.Fields);

        idolDocumentsService.getDocumentContent(getContentRequest);
        verify(queryResponseParser).parseQueryHits(responseData.getHits());
    }

    @Test
    public void getStateToken() {
        when(queryExecutor.executeQuery(any(), any())).thenReturn(mockStateTokenResponse());

        final String stateToken = idolDocumentsService.getStateToken(mockQueryParams(QueryRequest.QueryType.RAW).getQueryRestrictions(), 3, QueryRequest.QueryType.RAW, false);
        assertThat(stateToken, is(MOCK_STATE_TOKEN));
    }

    @Test
    public void getStateTokenAndResultCount() {
        when(queryExecutor.executeQuery(any(), eq(QueryRequest.QueryType.RAW)))
            .thenReturn(mockStateTokenResponse());
        final StateTokenAndResultCount stateTokenAndResultCount =
            idolDocumentsService.getStateTokenAndResultCount(
                mockQueryParams(QueryRequest.QueryType.RAW).getQueryRestrictions(),
                3, QueryRequest.QueryType.RAW, false);

        verify(parameterHandler, Mockito.never()).addQmsParameters(any(), any());
        assertThat(stateTokenAndResultCount.getTypedStateToken().getStateToken(), is(MOCK_STATE_TOKEN));
        assertThat(stateTokenAndResultCount.getResultCount(), is((long)MOCK_TOTAL_HITS));
    }

    @Test
    public void getStateTokenAndResultCount_modified() {
        when(queryExecutor.executeQuery(any(), eq(QueryRequest.QueryType.MODIFIED)))
            .thenReturn(mockStateTokenResponse());
        final IdolQueryRestrictions queryRestrictions =
            mockQueryParams(QueryRequest.QueryType.MODIFIED).getQueryRestrictions();
        final StateTokenAndResultCount stateTokenAndResultCount =
            idolDocumentsService.getStateTokenAndResultCount(
                queryRestrictions, 3, QueryRequest.QueryType.MODIFIED, false);

        verify(parameterHandler, Mockito.times(2)).addQmsParameters(any(), eq(queryRestrictions));
        assertThat(stateTokenAndResultCount.getTypedStateToken().getStateToken(), is(MOCK_STATE_TOKEN));
        assertThat(stateTokenAndResultCount.getResultCount(), is((long)MOCK_TOTAL_HITS));
    }

    private IdolQueryRequest mockQueryParams(final QueryRequest.QueryType queryType) {
        when(queryRestrictions.getQueryText()).thenReturn("*");
        when(queryRestrictions.getDatabases()).thenReturn(Arrays.asList("Database1", "Database2"));
        when(queryRestrictions.getMaxDate()).thenReturn(ZonedDateTime.now());

        when(queryRequest.getQueryRestrictions()).thenReturn(queryRestrictions);
        when(queryRequest.getStart()).thenReturn(1);
        when(queryRequest.getMaxResults()).thenReturn(50);
        when(queryRequest.getSummary()).thenReturn(SummaryParam.Concept.name());
        when(queryRequest.getSummaryCharacters()).thenReturn(250);
        when(queryRequest.isHighlight()).thenReturn(true);
        when(queryRequest.isAutoCorrect()).thenReturn(true);
        when(queryRequest.getPrint()).thenReturn(PrintParam.Fields.name());
        when(queryRequest.getQueryType()).thenReturn(queryType);

        return queryRequest;
    }

    private QueryResponseData mockStateTokenResponse() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setState(MOCK_STATE_TOKEN);
        responseData.setTotalhits(MOCK_TOTAL_HITS);
        return responseData;
    }
}
