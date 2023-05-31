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
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.answer.GetStatusResponsedata;
import com.hp.autonomy.types.idol.responses.answer.System;
import com.hp.autonomy.types.requests.idol.actions.status.StatusActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
class AnswerServerSystemServiceImpl implements AnswerServerSystemService {
    private final AciService aciService;
    private final AciService answerServerAciService;
    private final Processor<GetStatusResponsedata> processor;

    @Autowired
    AnswerServerSystemServiceImpl(final AciService aciService,
                                  final AciService answerServerAciService,
                                  final ProcessorFactory processorFactory) {
        this.aciService = aciService;
        this.answerServerAciService = answerServerAciService;
        processor = processorFactory.getResponseDataProcessor(GetStatusResponsedata.class);
    }

    @Override
    public Collection<String> getSystemNames() {
        return answerServerAciService.executeAction(new AciParameters(StatusActions.GetStatus.name()), processor)
                .getSystems()
                .getSystem()
                .stream()
                .map(System::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> getSystemNames(final AciServerDetails aciServerDetails) {
        return aciService.executeAction(aciServerDetails, new AciParameters(StatusActions.GetStatus.name()), processor)
                .getSystems()
                .getSystem()
                .stream()
                .map(System::getName)
                .collect(Collectors.toList());
    }
}
