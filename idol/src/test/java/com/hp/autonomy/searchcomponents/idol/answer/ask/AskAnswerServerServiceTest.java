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

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.autonomy.aci.client.services.AciService;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.answer.AskAnswer;
import com.opentext.idol.types.responses.answer.AskAnswers;
import com.opentext.idol.types.responses.answer.AskResponsedata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AskAnswerServerServiceTest {
    @Mock
    private AciService answerServerAciService;
    @Mock
    private ProcessorFactory processorFactory;
    @Mock
    private AskAnswerServerRequest request;

    private AskAnswerServerService service;

    @Before
    public void setUp() {
        final AskResponsedata responsedata = new AskResponsedata();
        final AskAnswers answers = new AskAnswers();
        answers.getAnswer().add(new AskAnswer());
        responsedata.setAnswers(answers);
        when(answerServerAciService.executeAction(any(), any())).thenReturn(responsedata);

        service = new AskAnswerServerServiceImpl(answerServerAciService, processorFactory);
    }

    @Test
    public void ask() {
        when(request.getSystemNames()).thenReturn(Collections.singleton("answerbank0"));
        assertThat(service.ask(request), not(empty()));
    }

    @Test
    public void askButNoResults() {
        when(answerServerAciService.executeAction(any(), any())).thenReturn(new AskResponsedata());
        assertThat(service.ask(request), empty());
    }
}
