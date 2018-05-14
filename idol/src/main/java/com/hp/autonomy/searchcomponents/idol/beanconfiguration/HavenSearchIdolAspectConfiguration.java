/*
 * Copyright 2015-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.logging.IdolActionIdAspect;
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

    public static final String IDOL_ACTION_ID_ASPECT_BEAN_NAME = "idolActionIdAspect";
    public static final String IDOL_ACTION_ID_PROPERTY_KEY = "idol.log.actionid.enabled";

    @Bean(name = IDOL_ACTION_ID_ASPECT_BEAN_NAME)
    @ConditionalOnProperty(IDOL_ACTION_ID_PROPERTY_KEY)
    public IdolActionIdAspect createIdolActionIdAspect() {
        return new IdolActionIdAspect();
    }

    @Bean(name = IDOL_LOGGING_ASPECT_BEAN_NAME)
    @ConditionalOnProperty(IDOL_LOG_PROPERTY_KEY)
    @ConditionalOnMissingBean(name = IDOL_LOGGING_ASPECT_BEAN_NAME)
    public IdolLoggingAspect IdolLoggingAspect(final ConfigService<? extends IdolSearchCapable> configService,
                                               @Value(IDOL_LOG_TIMING_PROPERTY) final boolean timingEnabled) {
        return new IdolLoggingAspect(configService, timingEnabled);
    }
}
