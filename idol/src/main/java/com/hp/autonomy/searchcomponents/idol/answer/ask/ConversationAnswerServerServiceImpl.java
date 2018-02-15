/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.InputStreamActionParameter;
import com.autonomy.aci.client.util.ActionParameters;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.conversation.ConversePrompt;
import com.hp.autonomy.types.idol.responses.conversation.ConverseResponsedata;
import com.hp.autonomy.types.idol.responses.conversation.ManageResourcesResponsedata;
import com.hp.autonomy.types.idol.responses.conversation.ManagedResources;
import com.hp.autonomy.types.idol.responses.conversation.ManagementResult;
import com.hp.autonomy.types.requests.idol.actions.answer.AnswerServerActions;
import com.hp.autonomy.types.requests.idol.actions.answer.params.ConverseParams;
import com.hp.autonomy.types.requests.idol.actions.answer.params.ManageResourcesParams;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hp.autonomy.searchcomponents.idol.answer.ask.ConversationAnswerServerService.CONVERSATION_SERVICE_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.exceptions.codes.IdolErrorCodes.ANSWER_SERVER;

/**
 * Default idol implementation of {@link ConversationAnswerServerService}
 */
@Slf4j
@Service(CONVERSATION_SERVICE_BEAN_NAME)
@IdolService(ANSWER_SERVER)
class ConversationAnswerServerServiceImpl implements ConversationAnswerServerService {
    private final AciService answerServerAciService;
    private final Processor<ConverseResponsedata> processor;
    private final Processor<ManageResourcesResponsedata> manageProcessor;
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final ObjectMapper objectMapper;

    @Autowired
    ConversationAnswerServerServiceImpl(final AciService answerServerAciService,
                                        final ProcessorFactory processorFactory,
                                        final ConfigService<? extends IdolSearchCapable> configService,
                                        final ObjectMapper objectMapper) {
        this.answerServerAciService = answerServerAciService;
        processor = processorFactory.getResponseDataProcessor(ConverseResponsedata.class);
        manageProcessor = processorFactory.getResponseDataProcessor(ManageResourcesResponsedata.class);
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String conversationStart() {
        final String conversationSystemName = getSystemName();

        final ManagementOperation op = new ManagementOperation("add", "conversation_session", null);

        final ActionParameters params = new ActionParameters(AnswerServerActions.ManageResources.name());
        params.add(ManageResourcesParams.SystemName.name(), conversationSystemName);

        try {
            params.add(new InputStreamActionParameter(ManageResourcesParams.Data.name(),
                new ByteArrayInputStream(objectMapper.writeValueAsBytes(op))));
        }
        catch(JsonProcessingException e) {
            throw new Error("Unexpected JSON processing error", e);
        }

        final List<ManagementResult> result = answerServerAciService.executeAction(params, manageProcessor).getResult();

        if (!result.isEmpty()) {
            final List<ManagedResources> resources = result.get(0).getManagedResources();

            if (!resources.isEmpty()) {
                final List<String> ids = resources.get(0).getId();

                if (!ids.isEmpty()) {
                    return ids.get(0);
                }
            }
        }

        return null;
    }

    @Override
    public void conversationEnd(final String... sessionIds) {
        final String conversationSystemName = getSystemName();

        if (sessionIds.length == 0) {
            return;
        }

        final ManagementOperation op = new ManagementOperation("delete", "conversation_session", Arrays.asList(sessionIds));

        final ActionParameters params = new ActionParameters(AnswerServerActions.ManageResources.name());
        params.add(ManageResourcesParams.SystemName.name(), conversationSystemName);

        try {
            params.add(new InputStreamActionParameter(ManageResourcesParams.Data.name(),
                    new ByteArrayInputStream(objectMapper.writeValueAsBytes(op))));
        }
        catch(JsonProcessingException e) {
            throw new Error("Unexpected JSON processing error", e);
        }

        final List<ManagementResult> result = answerServerAciService.executeAction(params, manageProcessor).getResult();

        log.info("Conversations ended: {}", result);
    }

    @Override
    public List<ConversePrompt> converse(final ConversationRequest request) {
        final String conversationSystemName = getSystemName();

        final ActionParameters params = new ActionParameters(AnswerServerActions.Converse.name());
        params.add(ConverseParams.Text.name(), request.getText());
        params.add(ConverseParams.SystemName.name(), conversationSystemName);
        params.add(ConverseParams.SessionId.name(), request.getSessionId());

        final List<ConversePrompt> answers = answerServerAciService.executeAction(params, processor).getPrompts();
        return Optional.ofNullable(answers).orElse(Collections.emptyList());
    }

    private String getSystemName() {
        final String conversationSystemName = configService.getConfig().getAnswerServer().getConversationSystemName();

        if(StringUtils.isBlank(conversationSystemName)) {
            throw new UnsupportedOperationException("Conversation system is not configured");
        }
        return conversationSystemName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ManagementOperation {
        final String operation;
        final String type;
        final List<String> ids;
    }
}
