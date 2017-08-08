/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.answer.AskAnswer;
import com.hp.autonomy.types.idol.responses.answer.AskAnswers;
import com.hp.autonomy.types.idol.responses.answer.AskResponsedata;
import com.hp.autonomy.types.requests.idol.actions.answer.AnswerServerActions;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskParams;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskSortParam;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<AskAnswer> ask(final AskAnswerServerRequest request) {
        final AciParameters aciParameters = new AciParameters(AnswerServerActions.Ask.name());
        aciParameters.add(AskParams.Text.name(), request.getText());
        aciParameters.add(AskParams.Sort.name(), Optional.ofNullable(request.getSort()).map(AskSortParam::value).orElse(null));
        aciParameters.add(AskParams.SystemNames.name(), String.join(",", request.getSystemNames()));
        aciParameters.add(AskParams.FirstResult.name(), request.getFirstResult());
        aciParameters.add(AskParams.MaxResults.name(), request.getMaxResults());
        aciParameters.add(AskParams.MinScore.name(), request.getMinScore());
        aciParameters.add("customizationData", request.getCustomizationData());

        final Map<String, String> proxiedParams = request.getProxiedParams();
        if (proxiedParams != null) {
            for(Map.Entry<String, String> entry : proxiedParams.entrySet()) {
                aciParameters.put(entry.getKey(), entry.getValue());
            }
        }

        final AskAnswers answers = answerServerAciService.executeAction(aciParameters, processor).getAnswers();
        return Optional.ofNullable(answers).map(AskAnswers::getAnswer).orElse(Collections.emptyList());
    }
}
