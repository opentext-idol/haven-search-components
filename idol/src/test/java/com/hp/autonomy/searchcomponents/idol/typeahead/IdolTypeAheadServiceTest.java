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

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdolTypeAheadServiceTest {
    @Mock
    private ConfigService<IdolSearchCapable> configService;

    @Mock
    private TypeAheadService<AciErrorException> termExpandTypeAheadService;

    @Mock
    private TypeAheadService<AciErrorException> qmsTypeAheadService;

    @Mock
    private IdolSearchCapable config;

    private TypeAheadService<AciErrorException> typeAheadService;

    @BeforeEach
    public void setUp() {
        typeAheadService = new IdolTypeAheadServiceImpl(configService, termExpandTypeAheadService, qmsTypeAheadService);
        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void getQmsSuggestions() {
        when(config.getQueryManipulation()).thenReturn(QueryManipulation.builder().enabled(true).build());

        final String text = "A";
        typeAheadService.getSuggestions(text);
        verify(qmsTypeAheadService).getSuggestions(text);
    }

    @Test
    public void getContentSuggestions() {
        when(config.getQueryManipulation()).thenReturn(QueryManipulation.builder().enabled(false).build());

        final String text = "A";
        typeAheadService.getSuggestions(text);
        verify(termExpandTypeAheadService).getSuggestions(text);
    }
}
