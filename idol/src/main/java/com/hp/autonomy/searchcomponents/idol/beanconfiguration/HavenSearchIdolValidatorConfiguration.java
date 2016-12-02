/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.validation.Validator;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfig;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfigValidator;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulationValidator;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfigValidator;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable.ANSWER_SERVER_VALIDATOR_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable.QUERY_MANIPULATION_VALIDATOR_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable.VIEW_CONFIG_VALIDATOR_BEAN_NAME;

/**
 * Overridable validators
 */
@Configuration
public class HavenSearchIdolValidatorConfiguration {
    @Bean(name = QUERY_MANIPULATION_VALIDATOR_BEAN_NAME)
    @ConditionalOnMissingBean(name = QUERY_MANIPULATION_VALIDATOR_BEAN_NAME)
    public Validator<QueryManipulation> queryManipulationValidator(final AciService validatorAciService, final ProcessorFactory processorFactory) {
        return new QueryManipulationValidator(validatorAciService, processorFactory);
    }

    @Bean(name = ANSWER_SERVER_VALIDATOR_BEAN_NAME)
    @ConditionalOnMissingBean(name = ANSWER_SERVER_VALIDATOR_BEAN_NAME)
    public Validator<AnswerServerConfig> answerServerValidator(final AciService validatorAciService, final ProcessorFactory processorFactory) {
        return new AnswerServerConfigValidator(validatorAciService, processorFactory);
    }

    @Bean(name = VIEW_CONFIG_VALIDATOR_BEAN_NAME)
    @ConditionalOnMissingBean(name = VIEW_CONFIG_VALIDATOR_BEAN_NAME)
    public Validator<ViewConfig> viewConfigValidator(final AciService validatorAciService, final ProcessorFactory processorFactory) {
        return new ViewConfigValidator(validatorAciService, processorFactory);
    }
}
