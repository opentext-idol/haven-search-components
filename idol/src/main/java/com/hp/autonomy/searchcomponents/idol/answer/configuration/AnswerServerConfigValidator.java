/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.searchcomponents.idol.answer.configuration;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import com.hp.autonomy.frontend.configuration.validation.Validator;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.answer.GetStatusResponsedata;
import com.hp.autonomy.types.requests.idol.actions.answer.AnswerServerActions;
import org.apache.commons.lang.StringUtils;

import static com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfigValidator.Validation.INVALID_CONVERSATION_SYSTEM_NAME;
import static com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfigValidator.Validation.INVALID_SYSTEM_NAME;

public class AnswerServerConfigValidator implements Validator<AnswerServerConfig> {
    private final AciService aciService;
    private final ProcessorFactory processorFactory;

    public AnswerServerConfigValidator(final AciService validatorAciService,
                                       final ProcessorFactory processorFactory) {
        aciService = validatorAciService;
        this.processorFactory = processorFactory;
    }

    @Override
    public ValidationResult<?> validate(final AnswerServerConfig config) {
        final ServerConfig server = config.getServer();
        final ValidationResult<?> validate = server.validate(aciService, null, processorFactory);

        final String conversationSystemName = config.getConversationSystemName();
        if (StringUtils.isNotBlank(conversationSystemName) && validate.isValid()) {
            final GetStatusResponsedata systems = aciService.executeAction(
                    server.toAciServerDetails(),
                    new ActionParameters(AnswerServerActions.GetStatus.name()),
                    processorFactory.getResponseDataProcessor(GetStatusResponsedata.class));

            if (systems.getSystems().getSystem().stream().noneMatch(
                    s -> conversationSystemName.equals(s.getName())
                    && "conversation".equals(s.getType())
            )) {
                return new ValidationResult<>(false, INVALID_CONVERSATION_SYSTEM_NAME);
            }
        }

        if (config.getSystemNames() != null && !config.getSystemNames().isEmpty() && validate.isValid()) {
            final GetStatusResponsedata systems = aciService.executeAction(
                    server.toAciServerDetails(),
                    new ActionParameters(AnswerServerActions.GetStatus.name()),
                    processorFactory.getResponseDataProcessor(GetStatusResponsedata.class));

            for(String systemName : config.getSystemNames()) {
                if (systems.getSystems().getSystem().stream().noneMatch(
                        s -> systemName.equals(s.getName())
                )) {
                    return new ValidationResult<>(false, INVALID_SYSTEM_NAME);
                }
            }
        }

        return validate;
    }

    @Override
    public Class<AnswerServerConfig> getSupportedClass() {
        return AnswerServerConfig.class;
    }

    public enum Validation {
        INVALID_CONVERSATION_SYSTEM_NAME,
        INVALID_SYSTEM_NAME
    }
}
