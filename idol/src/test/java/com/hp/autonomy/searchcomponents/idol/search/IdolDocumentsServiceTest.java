/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.types.idol.responses.Hit;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.idol.responses.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    private IdolDocumentsService idolDocumentsService;

    @Before
    public void setUp() {
        idolDocumentsService = new IdolDocumentsService(parameterHandler, queryExecutor, queryResponseParser);
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

        final IdolQueryRestrictions queryRestrictions = IdolQueryRestrictions.builder().build();
        final SuggestRequest<String> suggestRequest = SuggestRequest.<String>builder()
                .reference("Some reference")
                .queryRestrictions(queryRestrictions)
                .start(1)
                .maxResults(50)
                .summary(SummaryParam.Context.name())
                .summaryCharacters(250)
                .sort(null)
                .highlight(true)
                .print(PrintParam.Fields.name())
                .build();
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

        final GetContentRequest<String> getContentRequest = GetContentRequest.<String>builder()
                .indexAndReferences(new GetContentRequestIndex<>("Database1", Collections.singleton("Some reference")))
                .print(PrintParam.Fields.name())
                .build();
        idolDocumentsService.getDocumentContent(getContentRequest);
        verify(queryResponseParser).parseQueryHits(responseData.getHits());
    }

    @Test
    public void getStateToken() {
        when(queryExecutor.executeQuery(any(), any())).thenReturn(mockStateTokenResponse());

        final String stateToken = idolDocumentsService.getStateToken(mockQueryParams(QueryRequest.QueryType.RAW).getQueryRestrictions(), 3, false);
        assertThat(stateToken, is(MOCK_STATE_TOKEN));
    }

    @Test
    public void getStateTokenAndResultCount() {
        when(queryExecutor.executeQuery(any(), any())).thenReturn(mockStateTokenResponse());

        final StateTokenAndResultCount stateTokenAndResultCount = idolDocumentsService.getStateTokenAndResultCount(mockQueryParams(QueryRequest.QueryType.RAW).getQueryRestrictions(), 3, false);
        assertThat(stateTokenAndResultCount.getTypedStateToken().getStateToken(), is(MOCK_STATE_TOKEN));
        assertThat(stateTokenAndResultCount.getResultCount(), is((long) MOCK_TOTAL_HITS));
    }

    // Used in Find's DocumentService test
    private QueryRequest<String> mockQueryParams(final QueryRequest.QueryType queryType) {
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder()
                .queryText("*")
                .databases(Arrays.asList("Database1", "Database2"))
                .maxDate(DateTime.now())
                .build();
        return QueryRequest.<String>builder()
                .queryRestrictions(queryRestrictions)
                .start(1)
                .maxResults(50)
                .summary(SummaryParam.Concept.name())
                .summaryCharacters(250)
                .sort(null)
                .highlight(true)
                .autoCorrect(true)
                .print(PrintParam.Fields.name())
                .queryType(queryType)
                .build();
    }

    private QueryResponseData mockStateTokenResponse() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setState(MOCK_STATE_TOKEN);
        responseData.setTotalhits(MOCK_TOTAL_HITS);
        return responseData;
    }
}
