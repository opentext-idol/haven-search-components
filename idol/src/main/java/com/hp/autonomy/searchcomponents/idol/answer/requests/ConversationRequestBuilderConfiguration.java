/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.hp.autonomy.searchcomponents.idol.answer.ask.ConversationRequestBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static com.hp.autonomy.searchcomponents.idol.answer.ask.ConversationAnswerServerService.CONVERSATION_REQUEST_BUILDER_BEAN_NAME;

/**
 * Defines default beans for builders for AnswerServer conversation request objects
 */
@Configuration
public class ConversationRequestBuilderConfiguration {
    @Bean(name = CONVERSATION_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = CONVERSATION_REQUEST_BUILDER_BEAN_NAME)
    public ConversationRequestBuilder getConversationRequestBuilder() {
        return ConversationRequestImpl.builder();
    }
}
