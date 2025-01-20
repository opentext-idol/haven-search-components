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

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AciServiceRetrieverTest {
    @Mock
    private ConfigService<IdolSearchCapable> configService;
    @Mock
    private IdolSearchCapable config;
    @Mock
    private AciService contentAciService;
    @Mock
    private AciService qmsAciService;

    private AciServiceRetriever aciServiceRetriever;

    @BeforeEach
    public void setUp() {
        aciServiceRetriever = new AciServiceRetrieverImpl(configService, contentAciService, qmsAciService);

        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void qmsEnabled() {
        enableQms();
        assertTrue(aciServiceRetriever.qmsEnabled());
    }

    @Test
    public void getModifiedAciServiceQmsDisabled() {
        assertEquals(contentAciService, aciServiceRetriever.getAciService(QueryRequest.QueryType.MODIFIED));
    }

    @Test
    public void getModifiedAciServiceQmsEnabled() {
        enableQms();
        assertEquals(qmsAciService, aciServiceRetriever.getAciService(QueryRequest.QueryType.MODIFIED));
    }

    @Test
    public void getRawAciServiceQmsEnabled() {
        enableQms();
        assertEquals(contentAciService, aciServiceRetriever.getAciService(QueryRequest.QueryType.RAW));
    }

    private void enableQms() {
        when(config.getQueryManipulation()).thenReturn(QueryManipulation.builder()
                .enabled(true)
                .build());
    }
}
