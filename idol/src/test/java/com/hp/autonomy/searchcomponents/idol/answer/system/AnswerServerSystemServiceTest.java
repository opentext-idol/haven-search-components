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

package com.hp.autonomy.searchcomponents.idol.answer.system;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.answer.GetStatusResponsedata;
import com.opentext.idol.types.responses.answer.System;
import com.opentext.idol.types.responses.answer.Systems;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServerSystemServiceTest {
    @Mock
    private AciService aciService;
    @Mock
    private AciService answerServerAciService;
    @Mock
    protected ProcessorFactory processorFactory;
    @Mock
    protected Processor<GetStatusResponsedata> getResourcesProcessor;

    private AnswerServerSystemService answerServerSystemService;

    @Before
    public void setUp() {
        answerServerSystemService = new AnswerServerSystemServiceImpl(aciService, answerServerAciService, processorFactory);
    }

    @Test
    public void getSystemNames() {
        when(aciService.executeAction(any(), any(), any())).thenReturn(mockStatus());
        assertThat(answerServerSystemService.getSystemNames(new AciServerDetails()), not(empty()));
    }

    @Test
    public void getSystemNamesViaConfiguration() {
        when(answerServerAciService.executeAction(any(), any())).thenReturn(mockStatus());
        assertThat(answerServerSystemService.getSystemNames(), not(empty()));
    }

    private GetStatusResponsedata mockStatus() {
        final GetStatusResponsedata answerbankStatus = new GetStatusResponsedata();
        final Systems systems = new Systems();
        final System system = new System();
        system.setName("answerbank0");
        systems.getSystem().add(system);
        answerbankStatus.setSystems(systems);
        return answerbankStatus;
    }
}
