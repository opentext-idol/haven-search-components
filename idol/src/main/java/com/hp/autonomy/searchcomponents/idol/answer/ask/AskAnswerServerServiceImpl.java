/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.idol.exceptions.IdolService;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.answer.Answer;
import com.hp.autonomy.types.idol.responses.answer.Answers;
import com.hp.autonomy.types.idol.responses.answer.AskResponsedata;
import com.hp.autonomy.types.requests.idol.actions.answer.AnswerServerActions;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerService.ASK_SERVICE_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.exceptions.codes.IdolErrorCodes.ANSWER_SERVER;

/**
 * Default idol implementation of {@link AskAnswerServerService}
 */
@Service(ASK_SERVICE_BEAN_NAME)
@IdolService(ANSWER_SERVER)
class AskAnswerServerServiceImpl implements AskAnswerServerService {
    private final AciService answerServerAciService;
    private final Processor<AskResponsedata> processor;

    @Autowired
    AskAnswerServerServiceImpl(final AciService answerServerAciService,
                               final ProcessorFactory processorFactory) {
        this.answerServerAciService = answerServerAciService;
        processor = processorFactory.getResponseDataProcessor(AskResponsedata.class);
    }

    @SuppressWarnings("IfMayBeConditional")
    @Override
    public List<Answer> ask(final AskAnswerServerRequest request) {
        final AciParameters aciParameters = new AciParameters(AnswerServerActions.Ask.name());
        aciParameters.add(AskParams.Text.name(), request.getText());
        aciParameters.add(AskParams.SystemNames.name(), String.join(",", request.getSystemNames()));
        aciParameters.add(AskParams.MaxResults.name(), request.getMaxResults());

        final Answers answers = answerServerAciService.executeAction(aciParameters, processor).getAnswers();
        return Optional.ofNullable(answers).map(Answers::getAnswer).orElse(Collections.emptyList());
    }
}
