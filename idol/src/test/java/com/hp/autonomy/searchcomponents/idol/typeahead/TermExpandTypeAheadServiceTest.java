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
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.TermExpandResponseData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermExpandTypeAheadServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private ProcessorFactory processorFactory;

    private TermExpandTypeAheadService termExpandTypeAheadService;

    @Before
    public void setUp() {
        termExpandTypeAheadService = new TermExpandTypeAheadService(contentAciService, processorFactory);
    }

    @Test
    public void getSuggestions() {
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(mockResponse());
        final List<String> suggestions = termExpandTypeAheadService.getSuggestions("A");
        assertEquals("ab", suggestions.get(0));
    }

    private TermExpandResponseData mockResponse() {
        final TermExpandResponseData typeAheadResponseData = new TermExpandResponseData();
        final TermExpandResponseData.Term term = new TermExpandResponseData.Term();
        term.setValue("Ab");
        typeAheadResponseData.getTerm().add(term);
        return typeAheadResponseData;
    }
}
