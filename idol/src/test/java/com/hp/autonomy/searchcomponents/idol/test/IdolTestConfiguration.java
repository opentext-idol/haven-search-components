/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.test;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class IdolTestConfiguration {
    @Bean
    public ConfigService<HavenSearchCapable> configService() {
        @SuppressWarnings("unchecked") final ConfigService<HavenSearchCapable> configService = (ConfigService<HavenSearchCapable>) mock(ConfigService.class);
        final HavenSearchCapable config = mock(HavenSearchCapable.class);
        when(config.getContentAciServerDetails()).thenReturn(new AciServerDetails("find-idol", 9000));
        when(config.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(false).build());
        when(configService.getConfig()).thenReturn(config);

        return configService;
    }
}
