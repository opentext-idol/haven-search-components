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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class IdolTestConfiguration {
    public static final String IDOL_HOST = "iso-idol";
    public static final int CONTENT_PORT = 9000;
    public static final int VIEW_SERVER_PORT = 9080;

    private static final String SAMPLE_REFERENCE_FIELD_NAME = "DREREFERENCE";

    @Bean
    @ConditionalOnMissingBean(ConfigService.class)
    public ConfigService<IdolSearchCapable> configService() {
        @SuppressWarnings("unchecked") final ConfigService<IdolSearchCapable> configService = (ConfigService<IdolSearchCapable>) mock(ConfigService.class);

        final IdolSearchCapable config = mock(IdolSearchCapable.class);

        when(config.getContentAciServerDetails()).thenReturn(new AciServerDetails(IDOL_HOST, CONTENT_PORT));
        when(config.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(false).build());

        final ViewConfig viewConfig = new ViewConfig.Builder().setReferenceField(SAMPLE_REFERENCE_FIELD_NAME).setHost(IDOL_HOST).setPort(VIEW_SERVER_PORT).build();
        when(config.getViewConfig()).thenReturn(viewConfig);

        when(config.getFieldsInfo()).thenReturn(new FieldsInfo.Builder().build());

        when(configService.getConfig()).thenReturn(config);

        return configService;
    }
}
