/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.searchcomponents.idol.answer.configuration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import com.hp.autonomy.frontend.configuration.validation.Validator;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable.ANSWER_SERVER_VALIDATOR_BEAN_NAME;

@Component(ANSWER_SERVER_VALIDATOR_BEAN_NAME)
class AnswerServerConfigValidator implements Validator<AnswerServerConfig> {
    private final AciService aciService;
    private final ProcessorFactory processorFactory;

    @Autowired
    AnswerServerConfigValidator(final AciService validatorAciService,
                                final ProcessorFactory processorFactory) {
        aciService = validatorAciService;
        this.processorFactory = processorFactory;
    }

    @Override
    public ValidationResult<?> validate(final AnswerServerConfig config) {
        final ServerConfig server = config.getServer();
        return server.validate(aciService, null, processorFactory);
    }

    @Override
    public Class<AnswerServerConfig> getSupportedClass() {
        return AnswerServerConfig.class;
    }
}
