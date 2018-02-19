/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.ActionParameter;
import com.autonomy.aci.client.util.ActionParameters;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private final ObjectMapper logMapper;

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
        // Custom log mapper to censor out the SECURITY_INFO property when written to idol-access.log.
        logMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addSerializer(SessionVariable.class, new CensoredJsonSessionSerializer());
        logMapper.registerModule(module);
    }

    @Override
    public String conversationStart(final Map<String, String> sessionAttributes) {
        final String conversationSystemName = getSystemName();

        final ConversationStart op = new ConversationStart(sessionAttributes);

        final ActionParameters params = new ActionParameters(AnswerServerActions.ManageResources.name());
        params.add(ManageResourcesParams.SystemName.name(), conversationSystemName);

        try {
            params.add(new JsonMultipartString(ManageResourcesParams.Data.name(), op));
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

        final ConversationEnd op = new ConversationEnd(Arrays.asList(sessionIds));

        final ActionParameters params = new ActionParameters(AnswerServerActions.ManageResources.name());
        params.add(ManageResourcesParams.SystemName.name(), conversationSystemName);

        try {
            params.add(new JsonMultipartString(ManageResourcesParams.Data.name(), op));
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
    private static class ConversationStart {
        final String operation = "add";
        final String type = "conversation_session";
        final List<SessionVariable> session_variables;

        ConversationStart(final Map<String, String> sessionAttributes) {

            if (CollectionUtils.isEmpty(sessionAttributes)) {
                session_variables = null;
            }
            else {
                session_variables = sessionAttributes.entrySet().stream()
                    .map(entry -> new SessionVariable(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            }
        }
    }

    @Data
    private static class SessionVariable {
        final String name;
        final String value;
    }

    private static class CensoredJsonSessionSerializer extends JsonSerializer<SessionVariable> {
        @Override
        public void serialize(final SessionVariable value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("name", value.getName());
            gen.writeStringField("value", "SECURITY_INFO".equalsIgnoreCase(value.getName()) ? "*******" : value.getValue());
            gen.writeEndObject();
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ConversationEnd {
        final String operation = "delete";
        final String type = "conversation_session";
        final List<String> ids;
    }

    public class JsonMultipartString implements ActionParameter<Object> {

        private final String name;
        private final Object value;
        private final String json;
        private final String censoredJson;

        public JsonMultipartString(final String name, final Object value) throws JsonProcessingException {
            this.name = name;
            this.value = value;
            this.json = objectMapper.writeValueAsString(value);
            this.censoredJson = logMapper.writeValueAsString(value);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            // Use censored version for logging.
            return value == null ? null : censoredJson;
        }

        @Override
        public void addToEntity(final MultipartEntityBuilder builder, final Charset charset) {
            // Use the real value when sending to answer server.
            builder.addPart(getName(), new StringBody(json, ContentType.create("application/json", charset)));
        }

        @Override
        public boolean requiresPostRequest() {
            return true;
        }
    }
}
