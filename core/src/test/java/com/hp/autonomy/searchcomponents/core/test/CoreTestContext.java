/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;

@SuppressWarnings("UtilityClass")
@Configuration
@ComponentScan("com.hp.autonomy.searchcomponents.core")
@ConditionalOnProperty(CORE_CLASSES_PROPERTY)
public class CoreTestContext {
    public static final String CORE_CLASSES_PROPERTY = "core-classes";
}
