/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolComponentLabelLookup;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default service to create {@link IdolComponentLabelLookup} from the config service.
 */
@SuppressWarnings("WeakerAccess")
@Configuration
public class HavenSearchIdolComponentLabelLookupConfiguration {

    @Bean
    @ConditionalOnMissingBean(IdolComponentLabelLookup.class)
    public IdolComponentLabelLookup createIdolComponentLabelLookup (
        final ConfigService<? extends IdolSearchCapable> configService
    ) {
        return (host, port) -> configService.getConfig().lookupComponentNameByHostAndPort(host, port);
    }
}
