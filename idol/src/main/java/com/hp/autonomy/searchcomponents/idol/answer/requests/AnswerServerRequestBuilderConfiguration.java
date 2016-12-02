/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequestBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerService.ASK_REQUEST_BUILDER_BEAN_NAME;

/**
 * Defines default beans for builders for AnswerServer request objects
 */
@Configuration
public class AnswerServerRequestBuilderConfiguration {
    @Bean(name = ASK_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = ASK_REQUEST_BUILDER_BEAN_NAME)
    public AskAnswerServerRequestBuilder getAskRequestBuilder() {
        return AskAnswerServerRequestImpl.builder();
    }
}
