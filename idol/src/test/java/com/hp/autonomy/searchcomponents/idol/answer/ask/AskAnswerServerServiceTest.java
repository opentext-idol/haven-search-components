/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.searchcomponents.idol.answer.system.AnswerServerSystemService;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.answer.Answer;
import com.hp.autonomy.types.idol.responses.answer.Answers;
import com.hp.autonomy.types.idol.responses.answer.AskResponsedata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AskAnswerServerServiceTest {
    @Mock
    private AciService answerServerAciService;
    @Mock
    private AnswerServerSystemService answerServerSystemService;
    @Mock
    private ProcessorFactory processorFactory;
    @Mock
    private AskAnswerServerRequest request;

    private AskAnswerServerService service;

    @Before
    public void setUp() {
        final AskResponsedata responsedata = new AskResponsedata();
        final Answers answers = new Answers();
        answers.getAnswer().add(new Answer());
        responsedata.setAnswers(answers);
        when(answerServerAciService.executeAction(any(), any())).thenReturn(responsedata);

        service = new AskAnswerServerServiceImpl(answerServerAciService, answerServerSystemService, processorFactory);
    }

    @Test
    public void ask() {
        when(request.getSystemNames()).thenReturn(Collections.singleton("answerbank0"));
        assertThat(service.ask(request), not(empty()));
        verify(answerServerSystemService, never()).getSystemNames();
    }

    @Test
    public void askWithoutSystemNames() {
        assertThat(service.ask(request), not(empty()));
        verify(answerServerSystemService).getSystemNames();
    }

    @Test
    public void askButNoResults() {
        when(answerServerAciService.executeAction(any(), any())).thenReturn(new AskResponsedata());
        assertThat(service.ask(request), empty());
    }
}
