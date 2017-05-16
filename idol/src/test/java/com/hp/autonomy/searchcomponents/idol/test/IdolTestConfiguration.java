/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.test;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfig;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@Configuration
@ConditionalOnProperty(value = "mock.configuration", matchIfMissing = true)
public class IdolTestConfiguration {
    public static final String CONTENT_HOST_PROPERTY = "test.content.host";
    public static final String CONTENT_HOST = "ida-idol";
    public static final String CONTENT_PORT_PROPERTY = "test.content.port";
    public static final int CONTENT_PORT = 9000;
    public static final String VIEW_SERVER_HOST_PROPERTY = "test.view.host";
    public static final String VIEW_SERVER_HOST = "ida-idol";
    public static final String VIEW_SERVER_PORT_PROPERTY = "test.view.port";
    public static final int VIEW_SERVER_PORT = 9080;
    public static final String VIEW_SERVER_REFERENCE_FIELD = "DREREFERENCE";
    public static final String ANSWER_SERVER_HOST_PROPERTY = "test.answer.host";
    public static final String ANSWER_SERVER_HOST = "ida-answer";
    public static final String ANSWER_SERVER_PORT_PROPERTY = "test.answer.port";
    public static final int ANSWER_SERVER_PORT = 7700;
    public static final String ANSWER_SERVER_SYSTEM_PROPERTY = "test.answer.system";
    public static final String ANSWER_SERVER_SYSTEM_NAME = "answerbank0";

    @Autowired
    private Environment environment;

    @Bean
    public IdolSearchCapable testConfig() {
        return mock(IdolSearchCapable.class);
    }

    @Bean
    public ConfigService<IdolSearchCapable> testConfigService(final IdolSearchCapable config) {
        final QueryManipulation queryManipulationConfig = QueryManipulation.builder()
                .enabled(false)
                .build();

        final ViewConfig viewConfig = ViewConfig.builder()
                .referenceField(VIEW_SERVER_REFERENCE_FIELD)
                .host(getProperty(VIEW_SERVER_HOST_PROPERTY, VIEW_SERVER_HOST))
                .port(getIntProperty(VIEW_SERVER_PORT_PROPERTY, VIEW_SERVER_PORT))
                .build();

        final AnswerServerConfig answerServerConfig = AnswerServerConfig.builder()
                .server(ServerConfig.builder()
                        .host(getProperty(ANSWER_SERVER_HOST_PROPERTY, ANSWER_SERVER_HOST))
                        .port(getIntProperty(ANSWER_SERVER_PORT_PROPERTY, ANSWER_SERVER_PORT))
                        .build())
                .systemName(getProperty(ANSWER_SERVER_SYSTEM_PROPERTY, ANSWER_SERVER_SYSTEM_NAME))
                .build();

        final AciServerDetails contentAciServerDetails = new AciServerDetails(
                getProperty(CONTENT_HOST_PROPERTY, CONTENT_HOST),
                getIntProperty(CONTENT_PORT_PROPERTY, CONTENT_PORT)
        );

        when(config.getContentAciServerDetails()).thenReturn(contentAciServerDetails);
        when(config.getQueryManipulation()).thenReturn(queryManipulationConfig);
        when(config.getViewConfig()).thenReturn(viewConfig);
        when(config.getAnswerServer()).thenReturn(answerServerConfig);
        when(config.getFieldsInfo()).thenReturn(FieldsInfo.builder().build());

        @SuppressWarnings("unchecked")
        final ConfigService<IdolSearchCapable> configService = mock(ConfigService.class);

        when(configService.getConfig()).thenReturn(config);

        return configService;
    }

    private String getProperty(final String property, final String defaultValue) {
        return environment.getProperty(property, defaultValue);
    }

    private int getIntProperty(final String property, final int defaultValue) {
        return Integer.parseInt(environment.getProperty(property, String.valueOf(defaultValue)));
    }
}
