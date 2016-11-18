/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

/**
 * Configuration required for performing search actions with this module
 */
@FunctionalInterface
public interface HavenSearchCapable {
    /**
     * A map of field information
     *
     * @return A map of field information
     */
    FieldsInfo getFieldsInfo();
}
