/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.logging.IdolLoggingAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Overridable aspects
 */
@SuppressWarnings("WeakerAccess")
@Configuration
public class HavenSearchIdolAspectConfiguration {
    public static final String IDOL_LOGGING_ASPECT_BEAN_NAME = "idolLoggingAspect";
    public static final String IDOL_LOG_PROPERTY_KEY = "idol.log.enabled";
    public static final String IDOL_LOG_TIMING_PROPERTY_KEY = "idol.log.timing.enabled";
    public static final String IDOL_LOG_TIMING_PROPERTY = "${" + IDOL_LOG_TIMING_PROPERTY_KEY + ":true}";

    @Bean(name = IDOL_LOGGING_ASPECT_BEAN_NAME)
    @ConditionalOnProperty(IDOL_LOG_PROPERTY_KEY)
    @ConditionalOnMissingBean(name = IDOL_LOGGING_ASPECT_BEAN_NAME)
    public IdolLoggingAspect IdolLoggingAspect(final ConfigService<? extends IdolSearchCapable> configService,
                                               @Value(IDOL_LOG_TIMING_PROPERTY) final boolean timingEnabled) {
        return new IdolLoggingAspect(configService, timingEnabled);
    }
}
