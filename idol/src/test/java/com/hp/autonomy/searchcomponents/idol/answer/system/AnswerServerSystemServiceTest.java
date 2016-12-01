/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.system;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.answer.AnswerserverGetStatus;
import com.hp.autonomy.types.idol.responses.answer.System;
import com.hp.autonomy.types.idol.responses.answer.Systems;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
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
    protected Processor<AnswerserverGetStatus> getResourcesProcessor;

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

    private AnswerserverGetStatus mockStatus() {
        final AnswerserverGetStatus answerbankStatus = new AnswerserverGetStatus();
        final Systems systems = new Systems();
        final System system = new System();
        system.setName("answerbank0");
        systems.getSystem().add(system);
        answerbankStatus.setSystems(systems);
        return answerbankStatus;
    }
}
