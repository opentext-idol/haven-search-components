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

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HavenSearchAciParameterHandlerProxyTest {
    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private IdolQueryRestrictions queryRestrictions;

    @Mock
    private IdolSearchRequest searchRequest;

    @Mock
    private IdolGetContentRequestIndex getContentRequestIndex;

    @Mock
    private IdolViewRequest viewRequest;

    private HavenSearchAciParameterHandler proxy;

    @Before
    public void setUp() {
        proxy = new HavenSearchAciParameterHandlerProxy() {
        };
        ReflectionTestUtils.setField(proxy, "parameterHandler", parameterHandler, HavenSearchAciParameterHandler.class);
    }

    @Test
    public void addSearchRestrictions() {
        final AciParameters aciParameters = new AciParameters();
        proxy.addSearchRestrictions(aciParameters, queryRestrictions);
        verify(parameterHandler).addSearchRestrictions(aciParameters, queryRestrictions);
    }

    @Test
    public void addSearchOutputParameters() {
        final AciParameters aciParameters = new AciParameters();
        proxy.addSearchOutputParameters(aciParameters, searchRequest);
        verify(parameterHandler).addSearchOutputParameters(aciParameters, searchRequest);
    }

    @Test
    public void addGetDocumentOutputParameters() {
        final AciParameters aciParameters = new AciParameters();
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
        proxy.addLanguageRestriction(aciParameters, queryRestrictions);
        verify(parameterHandler).addLanguageRestriction(aciParameters, queryRestrictions);
    }

    @Test
    public void addQmsParameters() {
        final AciParameters aciParameters = new AciParameters();
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
        proxy.addViewParameters(aciParameters, reference, viewRequest);
        verify(parameterHandler).addViewParameters(aciParameters, reference, viewRequest);
    }
}
