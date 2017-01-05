/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Generates display names for given field paths
 */
@FunctionalInterface
public interface FieldDisplayNameGenerator {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME = "fieldDisplayNameGenerator";

    /**
     * Generates a display name for the given field path
     *
     * @param normalisedFieldName fully normalised field path
     * @return the generated display name
     * @see FieldPathNormaliser#normaliseFieldPath(String)
     */
    String generateDisplayName(String normalisedFieldName);
}
