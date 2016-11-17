/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryExecutorTest {
    @Mock
    private AciServiceRetriever aciServiceRetriever;
    @Mock
    private AciService aciService;
    @Mock
    private ProcessorFactory processorFactory;

    private QueryExecutor queryExecutor;

    @Before
    public void setUp() {
        when(aciServiceRetriever.getAciService(any())).thenReturn(aciService);

        queryExecutor = new QueryExecutorImpl(aciServiceRetriever, processorFactory);
    }

    @Test
    public void performRawQuery() {
        assertTrue(queryExecutor.performQuery(QueryRequest.QueryType.RAW));
    }

    @Test
    public void performModifiedQuery() {
        assertTrue(queryExecutor.performQuery(QueryRequest.QueryType.MODIFIED));
    }

    @Test
    public void performPromotionsQueryNoQms() {
        assertFalse(queryExecutor.performQuery(QueryRequest.QueryType.PROMOTIONS));
    }

    @Test
    public void performPromotionsQueryAndQms() {
        when(aciServiceRetriever.qmsEnabled()).thenReturn(true);
        assertTrue(queryExecutor.performQuery(QueryRequest.QueryType.PROMOTIONS));
    }

    @Test
    public void executeQuery() {
        queryExecutor.executeQuery(new AciParameters(), QueryRequest.QueryType.MODIFIED);
        verify(aciService).executeAction(any(), any());
    }

    @Test
    public void executeSuggest() {
        queryExecutor.executeSuggest(new AciParameters(), QueryRequest.QueryType.RAW);
        verify(aciService).executeAction(any(), any());
    }
}
