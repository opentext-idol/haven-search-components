/*
 * Copyright 2015 Open Text.
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

package com.hp.autonomy.searchcomponents.core.test;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.mockito.Mockito.mock;

@SuppressWarnings("UtilityClass")
@Configuration
@ComponentScan("com.hp.autonomy.searchcomponents.core")
@ConditionalOnProperty(CORE_CLASSES_PROPERTY)
public class CoreTestContext {
    public static final String CORE_CLASSES_PROPERTY = "core-classes";

    @Bean
    @ConditionalOnMissingBean(FieldPathNormaliser.class)
    public FieldPathNormaliser fieldPathNormaliser() {
        return new AbstractFieldPathNormaliser() {
            @Override
            public FieldPath normaliseFieldPath(final String fieldPath) {
                return newFieldPath(fieldPath, fieldPath);
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Bean
    @ConditionalOnMissingBean(ConfigService.class)
    public ConfigService<HavenSearchCapable> configService() {
        final ConfigService<HavenSearchCapable> configService = mock(ConfigService.class);
        final HavenSearchCapable config = mock(HavenSearchCapable.class);
        Mockito.lenient().when(config.getFieldsInfo()).thenReturn(FieldsInfo.builder().build());
        Mockito.lenient().when(configService.getConfig()).thenReturn(config);
        return configService;
    }
}
