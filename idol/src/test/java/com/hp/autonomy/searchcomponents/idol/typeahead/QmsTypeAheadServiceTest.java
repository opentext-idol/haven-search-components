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

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.types.requests.qms.actions.typeahead.params.ModeParam;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.TypeAheadResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QmsTypeAheadServiceTest {
    @Mock
    private ConfigService<IdolSearchCapable> configService;

    @Mock
    private AciService qmsAciService;

    @Mock
    private ProcessorFactory processorFactory;

    @Mock
    private IdolSearchCapable config;

    @Mock
    private HavenSearchAciParameterHandler havenSearchAciParameterHandler;

    private QmsTypeAheadService qmsTypeAheadService;

    @BeforeEach
    public void setUp() {
        qmsTypeAheadService = new QmsTypeAheadService(configService, qmsAciService, processorFactory, havenSearchAciParameterHandler);
        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void getSuggestionsInDictionaryMode() {
        when(config.getQueryManipulation()).thenReturn(QueryManipulation.builder().typeAheadMode(ModeParam.Dictionary).build());
        when(qmsAciService.executeAction(Mockito.<AciParameter>anySet(), any())).thenReturn(mockResponse());
        final List<String> suggestions = qmsTypeAheadService.getSuggestions("A");
        assertEquals("Ab", suggestions.get(0));
    }

    @Test
    public void getSuggestionsInIndexMode() {
        when(config.getQueryManipulation()).thenReturn(QueryManipulation.builder().typeAheadMode(ModeParam.Index).build());
        when(qmsAciService.executeAction(Mockito.<AciParameter>anySet(), any())).thenReturn(mockResponse());
        final List<String> suggestions = qmsTypeAheadService.getSuggestions("A");
        assertEquals("ab", suggestions.get(0));
    }

    private TypeAheadResponseData mockResponse() {
        final TypeAheadResponseData typeAheadResponseData = new TypeAheadResponseData();
        final TypeAheadResponseData.Expansion expansion = new TypeAheadResponseData.Expansion();
        expansion.setScore(5);
        expansion.setValue("Ab");
        typeAheadResponseData.getExpansion().add(expansion);
        return typeAheadResponseData;
    }
}
