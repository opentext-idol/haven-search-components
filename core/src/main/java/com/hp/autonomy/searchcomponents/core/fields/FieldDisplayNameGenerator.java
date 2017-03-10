/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;

/**
 * Generates display names for given field paths
 */
public interface FieldDisplayNameGenerator {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME = "fieldDisplayNameGenerator";

    /**
     * Generates a display name for the given field path
     *
     * @param fieldPath field path
     * @return the generated display name
     * @see FieldPathNormaliser#normaliseFieldPath(String)
     */
    String generateDisplayName(FieldPath fieldPath);

    /**
     * Generates a display name for the given field id
     *
     * @param id a field id (as used in configuration to map multiple Idol fields together)
     * @return the generated display name
     * @see FieldPathNormaliser#normaliseFieldPath(String)
     */
    String generateDisplayNameFromId(String id);

    /**
     * Generates a display value for the given field path and value
     *
     * @param fieldPath field path
     * @param value     a field value
     * @param type      value type
     * @param <T>       field value type
     * @return a tag name object with a unique path id and a prettified display name
     */
    <T extends Serializable> String generateDisplayValue(FieldPath fieldPath, T value, FieldType type);

    /**
     * Generates a display value for the given field id and value
     *
     * @param id    a field id (as used in configuration to map multiple Idol fields together)
     * @param value a field value
     * @param type  value type
     * @param <T>   field value type
     * @return a tag name object with a unique path id and a prettified display name
     */
    <T extends Serializable> String generateDisplayValueFromId(String id, T value, FieldType type);
}
