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
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.TermExpandResponseData;
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
public class TermExpandTypeAheadServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private ProcessorFactory processorFactory;

    private TermExpandTypeAheadService termExpandTypeAheadService;

    @BeforeEach
    public void setUp() {
        termExpandTypeAheadService = new TermExpandTypeAheadService(contentAciService, processorFactory);
    }

    @Test
    public void getSuggestions() {
        when(contentAciService.executeAction(Mockito.<AciParameter>anySet(), any())).thenReturn(mockResponse());
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
