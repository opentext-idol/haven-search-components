/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
