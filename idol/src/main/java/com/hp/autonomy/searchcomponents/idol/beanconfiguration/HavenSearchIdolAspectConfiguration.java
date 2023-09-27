/*
 * Copyright 2015-2018 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.hp.autonomy.searchcomponents.idol.configuration.IdolComponentLabelLookup;
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
    public static final String IDOL_ACTION_ID_PREFIX_PROPERTY_KEY = "idol.log.actionid.prefix";
    public static final String IDOL_ACTION_ID_PREFIX_PROPERTY = "${" + IDOL_ACTION_ID_PREFIX_PROPERTY_KEY + ":}";

    @Bean(name = IDOL_ACTION_ID_ASPECT_BEAN_NAME)
    @ConditionalOnProperty(IDOL_ACTION_ID_PROPERTY_KEY)
    public IdolActionIdAspect createIdolActionIdAspect(
            @Value(IDOL_ACTION_ID_PREFIX_PROPERTY) final String prefix) {
        return new IdolActionIdAspect(prefix);
    }

    @Bean(name = IDOL_LOGGING_ASPECT_BEAN_NAME)
    @ConditionalOnProperty(IDOL_LOG_PROPERTY_KEY)
    @ConditionalOnMissingBean(name = IDOL_LOGGING_ASPECT_BEAN_NAME)
    public IdolLoggingAspect IdolLoggingAspect(@Value(IDOL_LOG_TIMING_PROPERTY) final boolean timingEnabled) {
        return new IdolLoggingAspect(timingEnabled);
    }
}
