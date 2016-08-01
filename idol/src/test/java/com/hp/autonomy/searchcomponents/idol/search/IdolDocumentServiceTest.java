/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.class)
public class IdolDocumentServiceTest {
    private static final String MOCK_STATE_TOKEN = "mock-state-token";
    private static final int MOCK_TOTAL_HITS = 42;

    @Mock
    protected HavenSearchAciParameterHandler parameterHandler;

    @Mock
    protected QueryResponseParser queryResponseParser;

    @Mock
    protected AciServiceRetriever aciServiceRetriever;

    @Mock
    protected AciService aciService;

    @Mock
    protected AciResponseJaxbProcessorFactory aciResponseProcessorFactory;

    protected IdolDocumentService idolDocumentService;

    @Before
    public void setUp() {
        when(aciServiceRetriever.getAciService(any(SearchRequest.QueryType.class))).thenReturn(aciService);
        idolDocumentService = new IdolDocumentService(parameterHandler, queryResponseParser, aciServiceRetriever, aciResponseProcessorFactory);
    }

    @Test
    public void queryContent() {
        when(aciServiceRetriever.qmsEnabled()).thenReturn(true);

        final QueryResponseData responseData = new QueryResponseData();
        when(aciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        idolDocumentService.queryTextIndex(mockQueryParams(SearchRequest.QueryType.RAW));
        verify(queryResponseParser).parseQueryResults(any(), any(AciParameters.class), eq(responseData), any());
    }

    @Test
    public void queryQms() {
        when(aciServiceRetriever.qmsEnabled()).thenReturn(true);

        final QueryResponseData responseData = new QueryResponseData();
        when(aciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        idolDocumentService.queryTextIndex(mockQueryParams(SearchRequest.QueryType.MODIFIED));
        verify(queryResponseParser).parseQueryResults(any(), any(AciParameters.class), eq(responseData), any());
    }

    @Test
    public void queryContentForPromotions() {
        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndex(mockQueryParams(SearchRequest.QueryType.PROMOTIONS));
        assertThat(results.getDocuments(), is(empty()));
    }

    @Test
    public void queryQmsForPromotions() {
        when(aciServiceRetriever.qmsEnabled()).thenReturn(true);

        final QueryResponseData responseData = new QueryResponseData();
        when(aciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        idolDocumentService.queryTextIndex(mockQueryParams(SearchRequest.QueryType.PROMOTIONS));
        verify(queryResponseParser).parseQueryResults(any(), any(AciParameters.class), eq(responseData), any());
    }

    @Test
    public void findSimilar() {
        final SuggestResponseData responseData = new SuggestResponseData();
        responseData.setTotalhits(1);
        final Hit hit = new Hit();
        responseData.getHits().add(hit);

        when(aciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        final IdolQueryRestrictions queryRestrictions = new IdolQueryRestrictions.Builder().build();
        final SuggestRequest<String> suggestRequest = new SuggestRequest.Builder<String>()
                .setReference("Some reference")
                .setQueryRestrictions(queryRestrictions)
                .setStart(1)
                .setMaxResults(50)
                .setSummary(SummaryParam.Context.name())
                .setSummaryCharacters(250)
                .setSort(null)
                .setHighlight(true)
                .setPrint(PrintParam.Fields.name())
                .build();
        idolDocumentService.findSimilar(suggestRequest);
        verify(queryResponseParser).parseQueryHits(responseData.getHits());
    }

    @Test
    public void getContent() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setTotalhits(1);
        final Hit hit = new Hit();
        responseData.getHits().add(hit);

        when(aciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        final GetContentRequest<String> getContentRequest = new GetContentRequest<>(Collections.singleton(new GetContentRequestIndex<>("Database1", Collections.singleton("Some reference"))), PrintParam.Fields.name());
        idolDocumentService.getDocumentContent(getContentRequest);
        verify(queryResponseParser).parseQueryHits(responseData.getHits());
    }

    @Test
    public void getStateToken() {
        when(aciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(mockStateTokenResponse());

        final String stateToken = idolDocumentService.getStateToken(mockQueryParams(SearchRequest.QueryType.RAW).getQueryRestrictions(), 3, false);
        assertThat(stateToken, is(MOCK_STATE_TOKEN));
    }

    @Test
    public void getStateTokenAndResultCount() {
        when(aciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(mockStateTokenResponse());

        final StateTokenAndResultCount stateTokenAndResultCount = idolDocumentService.getStateTokenAndResultCount(mockQueryParams(SearchRequest.QueryType.RAW).getQueryRestrictions(), 3, false);
        assertThat(stateTokenAndResultCount.getTypedStateToken().getStateToken(), is(MOCK_STATE_TOKEN));
        assertThat(stateTokenAndResultCount.getResultCount(), is((long) MOCK_TOTAL_HITS));
    }

    // Used in Find's DocumentService test
    protected SearchRequest<String> mockQueryParams(final SearchRequest.QueryType queryType) {
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("*").setDatabases(Arrays.asList("Database1", "Database2")).setMaxDate(DateTime.now()).build();
        return new SearchRequest.Builder<String>()
                .setQueryRestrictions(queryRestrictions)
                .setStart(1)
                .setMaxResults(50)
                .setSummary(SummaryParam.Concept.name())
                .setSummaryCharacters(250)
                .setSort(null)
                .setHighlight(true)
                .setAutoCorrect(true)
                .setPrint(PrintParam.Fields.name())
                .setQueryType(queryType)
                .build();
    }

    protected QueryResponseData mockStateTokenResponse() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setState(MOCK_STATE_TOKEN);
        responseData.setTotalhits(MOCK_TOTAL_HITS);
        return responseData;
    }
}
