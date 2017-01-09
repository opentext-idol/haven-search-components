/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.test;

import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UtilityClass")
@Configuration
@ComponentScan("com.hp.autonomy.searchcomponents.core")
@ConditionalOnProperty(CORE_CLASSES_PROPERTY)
public class CoreTestContext {
    public static final String CORE_CLASSES_PROPERTY = "core-classes";

    @Bean
    @ConditionalOnMissingBean(FieldPathNormaliser.class)
    public FieldPathNormaliser fieldPathNormaliser() {
        final FieldPathNormaliser fieldPathNormaliser = mock(FieldPathNormaliser.class);
        when(fieldPathNormaliser.normaliseFieldPath(anyString())).thenAnswer(x -> x.getArgumentAt(0, String.class));
        return fieldPathNormaliser;
    }
}
