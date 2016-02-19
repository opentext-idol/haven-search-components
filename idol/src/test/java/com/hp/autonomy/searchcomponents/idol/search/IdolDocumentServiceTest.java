/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolDocumentServiceTest {
    @Mock
    protected HavenSearchAciParameterHandler parameterHandler;

    @Mock
    protected IdolSearchCapable havenSearchConfig;

    @Mock
    protected QueryResponseParser queryResponseParser;

    @Mock
    protected ConfigService<IdolSearchCapable> configService;

    @Mock
    protected AciService contentAciService;

    @Mock
    protected AciService qmsAciService;

    @Mock
    protected AciResponseJaxbProcessorFactory aciResponseProcessorFactory;

    protected IdolDocumentService idolDocumentService;

    @Before
    public void setUp() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().build());
        when(configService.getConfig()).thenReturn(havenSearchConfig);

        idolDocumentService = new IdolDocumentService(configService, parameterHandler, queryResponseParser, contentAciService, qmsAciService, aciResponseProcessorFactory);
    }

    @Test
    public void queryContent() {
        final QueryResponseData responseData = new QueryResponseData();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        idolDocumentService.queryTextIndex(mockQueryParams());
        verify(queryResponseParser).parseQueryResults(Matchers.<AciSearchRequest<String>>any(), any(AciParameters.class), eq(responseData), any(IdolDocumentService.QueryExecutor.class));
    }

    @Test
    public void queryQms() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(true).build());
        final QueryResponseData responseData = new QueryResponseData();
        when(qmsAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        idolDocumentService.queryTextIndex(mockQueryParams());
        verify(queryResponseParser).parseQueryResults(Matchers.<AciSearchRequest<String>>any(), any(AciParameters.class), eq(responseData), any(IdolDocumentService.QueryExecutor.class));
    }

    @Test
    public void queryContentForPromotions() {
        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndexForPromotions(mockQueryParams());
        assertThat(results.getDocuments(), is(empty()));
    }

    @Test
    public void queryQmsForPromotions() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(true).build());
        final QueryResponseData responseData = new QueryResponseData();
        when(qmsAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        idolDocumentService.queryTextIndexForPromotions(mockQueryParams());
        verify(queryResponseParser).parseQueryResults(Matchers.<AciSearchRequest<String>>any(), any(AciParameters.class), eq(responseData), any(IdolDocumentService.QueryExecutor.class));
    }

    @Test
    public void findSimilar() {
        final SuggestResponseData responseData = new SuggestResponseData();
        responseData.setTotalhits(1);
        final Hit hit = new Hit();
        responseData.getHit().add(hit);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final SuggestRequest<String> suggestRequest = new SuggestRequest<>("Some reference", new IdolQueryRestrictions.Builder().build(), 1, 30, "context", 250, "relevance", true);
        idolDocumentService.findSimilar(suggestRequest);
        verify(queryResponseParser).parseQueryHits(responseData.getHit());
    }

    @Test
    public void getContent() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setTotalhits(1);
        final Hit hit = new Hit();
        responseData.getHit().add(hit);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final GetContentRequest<String> getContentRequest = new GetContentRequest<>(Collections.singleton(new GetContentRequestIndex<>("Database1", Collections.singleton("Some reference"))));
        idolDocumentService.getDocumentContent(getContentRequest);
        verify(queryResponseParser).parseQueryHits(responseData.getHit());
    }

    @Test
    public void getStateToken() {
        final String mockStateToken = "abc";
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setState(mockStateToken);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final String stateToken = idolDocumentService.getStateToken(mockQueryParams().getQueryRestrictions(), 42);
        assertEquals(mockStateToken, stateToken);
    }

    protected SearchRequest<String> mockQueryParams() {
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("*").setDatabases(Arrays.asList("Database1", "Database2")).setMaxDate(DateTime.now()).build();
        return new SearchRequest<>(queryRestrictions, 0, 50, null, 250, null, true, true, null);
    }
}
