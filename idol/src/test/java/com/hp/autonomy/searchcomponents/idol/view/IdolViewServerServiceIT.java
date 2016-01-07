/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.services.impl.AciServiceImpl;
import com.autonomy.aci.client.transport.AciResponseInputStream;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.transport.impl.AciHttpClientImpl;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import org.apache.http.client.HttpClient;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IdolViewServerServiceIT {
    private static final String SAMPLE_REFERENCE_FIELD_NAME = "DREREFERENCE";

    @Mock
    private ConfigService<? extends ViewCapable> configService;

    @Mock
    private ViewCapable viewCapableConfig;

    private IdolViewServerService idolViewServerService;

    @Before
    public void setUp() {
        final SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(90000)
                .build();

        final HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(120)
                .setDefaultSocketConfig(socketConfig)
                .build();

        final ViewConfig viewConfig = new ViewConfig.Builder().setReferenceField(SAMPLE_REFERENCE_FIELD_NAME).build();
        when(viewCapableConfig.getViewConfig()).thenReturn(viewConfig);
        when(configService.getConfig()).thenReturn(viewCapableConfig);

        final AciService contentAciService = new AciServiceImpl(new AciHttpClientImpl(httpClient), new AciServerDetails("abc-dev.hpswlabs.hp.com", 9000));
        final AciService viewAciService = new AciServiceImpl(new AciHttpClientImpl(httpClient), new AciServerDetails("abc-dev.hpswlabs.hp.com", 9080));

        final AciResponseJaxbProcessorFactory processorFactory = new AciResponseJaxbProcessorFactory();

        idolViewServerService = new IdolViewServerService(contentAciService, viewAciService, processorFactory, configService);
    }

    @Test
    public void viewDocument() throws ViewNoReferenceFieldException, ViewDocumentNotFoundException, ReferenceFieldBlankException {
        final Processor<?> processor = mock(Processor.class);
        idolViewServerService.viewDocument("http://washingtontimes.feedsportal.com/c/34503/f/629218/s/1fb85532/l/0L0Swashingtontimes0N0Cnews0C20A120Cmay0C250Chonoring0Eour0Efallen0Eby0Esupporting0Etheir0Eloved0Eones0C0Dutm0Isource0FRSS0IFeed0Gutm0Imedium0FRSS/story01.htm", Collections.<String>emptyList(), processor);
        verify(processor).process(any(AciResponseInputStream.class));
    }
}