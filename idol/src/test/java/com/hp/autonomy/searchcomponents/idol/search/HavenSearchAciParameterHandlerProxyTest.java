/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.view.ViewRequest;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HavenSearchAciParameterHandlerProxyTest {
    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    private HavenSearchAciParameterHandler proxy;

    @Before
    public void setUp() {
        proxy = new HavenSearchAciParameterHandlerProxy() {};
        ReflectionTestUtils.setField(proxy, "parameterHandler", parameterHandler, HavenSearchAciParameterHandler.class);
    }

    @Test
    public void addSearchRestrictions() {
        final AciParameters aciParameters = new AciParameters();
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder().build();
        proxy.addSearchRestrictions(aciParameters, queryRestrictions);
        verify(parameterHandler).addSearchRestrictions(aciParameters, queryRestrictions);
    }

    @Test
    public void addSearchOutputParameters() {
        final AciParameters aciParameters = new AciParameters();
        final SearchRequest<String> searchRequest = QueryRequest.<String>builder().build();
        proxy.addSearchOutputParameters(aciParameters, searchRequest);
        verify(parameterHandler).addSearchOutputParameters(aciParameters, searchRequest);
    }

    @Test
    public void addGetDocumentOutputParameters() {
        final AciParameters aciParameters = new AciParameters();
        final GetContentRequestIndex<String> getContentRequestIndex = new GetContentRequestIndex<>("SomeDatabase", Collections.singleton("SomeReference"));
        final PrintParam print = PrintParam.All;
        proxy.addGetDocumentOutputParameters(aciParameters, getContentRequestIndex, print);
        verify(parameterHandler).addGetDocumentOutputParameters(aciParameters, getContentRequestIndex, print);
    }

    @Test
    public void addGetContentOutputParameters() {
        final AciParameters aciParameters = new AciParameters();
        final String database = "SomeDatabase";
        final String reference = "SomeReference";
        final String referenceField = "SomeField";
        proxy.addGetContentOutputParameters(aciParameters, database, reference, referenceField);
        verify(parameterHandler).addGetContentOutputParameters(aciParameters, database, reference, referenceField);
    }

    @Test
    public void addLanguageRestriction() {
        final AciParameters aciParameters = new AciParameters();
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder().build();
        proxy.addLanguageRestriction(aciParameters, queryRestrictions);
        verify(parameterHandler).addLanguageRestriction(aciParameters, queryRestrictions);
    }

    @Test
    public void addQmsParameters() {
        final AciParameters aciParameters = new AciParameters();
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder().build();
        proxy.addQmsParameters(aciParameters, queryRestrictions);
        verify(parameterHandler).addQmsParameters(aciParameters, queryRestrictions);
    }

    @Test
    public void addSecurityInfo() {
        final AciParameters aciParameters = new AciParameters();
        proxy.addSecurityInfo(aciParameters);
        verify(parameterHandler).addSecurityInfo(aciParameters);
    }

    @Test
    public void addStoreStateParameters() {
        final AciParameters aciParameters = new AciParameters();
        proxy.addStoreStateParameters(aciParameters);
        verify(parameterHandler).addStoreStateParameters(aciParameters);
    }

    @Test
    public void addViewParameters() {
        final AciParameters aciParameters = new AciParameters();
        final String reference = "SomeReference";
        final ViewRequest<String> viewRequest = IdolViewRequest.builder().build();
        proxy.addViewParameters(aciParameters, reference, viewRequest);
        verify(parameterHandler).addViewParameters(aciParameters, reference, viewRequest);
    }
}
