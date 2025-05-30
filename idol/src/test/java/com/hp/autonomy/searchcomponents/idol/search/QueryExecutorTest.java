/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QueryExecutorTest {
    @Mock
    private AciServiceRetriever aciServiceRetriever;
    @Mock
    private AciService aciService;
    @Mock
    private ProcessorFactory processorFactory;

    private QueryExecutor queryExecutor;

    @BeforeEach
    public void setUp() {
        Mockito.lenient().when(aciServiceRetriever.getAciService(any())).thenReturn(aciService);

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
        queryExecutor.executeQuery(new ActionParameters(), QueryRequest.QueryType.MODIFIED);
        verify(aciService).executeAction(any(), any());
    }

    @Test
    public void executeSuggest() {
        queryExecutor.executeSuggest(new ActionParameters(), QueryRequest.QueryType.RAW);
        verify(aciService).executeAction(any(), any());
    }
}
