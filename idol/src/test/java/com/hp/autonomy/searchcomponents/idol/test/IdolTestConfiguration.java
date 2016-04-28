/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.test;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConditionalOnProperty(value = "mock.idol.configuration", matchIfMissing = true)
public class IdolTestConfiguration {
    private static final String DEFAULT_IDOL_HOST = "iso-idol";
    private static final int DEFAULT_CONTENT_PORT = 9000;
    private static final int DEFAULT_VIEW_SERVER_PORT = 9080;
    private static final String REFERENCE_FIELD_NAME = "DREREFERENCE";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    public ConfigService<IdolSearchCapable> configService() {
        final QueryManipulation queryManipulationConfig = new QueryManipulation.Builder()
                .setEnabled(false)
                .build();

        final ViewConfig viewConfig = new ViewConfig.Builder()
                .setReferenceField(REFERENCE_FIELD_NAME)
                .setHost(environment.getProperty("test.view.host", DEFAULT_IDOL_HOST))
                .setPort(Integer.parseInt(environment.getProperty("test.view.port", String.valueOf(DEFAULT_VIEW_SERVER_PORT))))
                .build();

        final AciServerDetails contentAciServerDetails = new AciServerDetails(
                environment.getProperty("test.content.host", DEFAULT_IDOL_HOST),
                Integer.parseInt(environment.getProperty("test.content.port", String.valueOf(DEFAULT_CONTENT_PORT)))
        );

        final IdolSearchCapable config = mock(IdolSearchCapable.class);

        when(config.getContentAciServerDetails()).thenReturn(contentAciServerDetails);
        when(config.getQueryManipulation()).thenReturn(queryManipulationConfig);
        when(config.getViewConfig()).thenReturn(viewConfig);
        when(config.getFieldsInfo()).thenReturn(new FieldsInfo.Builder().build());

        @SuppressWarnings("unchecked")
        final ConfigService<IdolSearchCapable> configService = mock(ConfigService.class);

        when(configService.getConfig()).thenReturn(config);

        return configService;
    }
}
