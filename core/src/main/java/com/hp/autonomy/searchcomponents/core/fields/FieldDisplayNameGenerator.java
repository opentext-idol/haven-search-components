/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

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
     * @return the display value
     */
    <T extends Serializable> String generateDisplayValue(FieldPath fieldPath, T value, FieldType type);

    /**
     * Generates a display value for the given field id and value
     *
     * @param id    a field id (as used in configuration to map multiple Idol fields together)
     * @param value a field value
     * @param type  value type
     * @param <T>   field value type
     * @return the display value
     */
    <T extends Serializable> String generateDisplayValueFromId(String id, T value, FieldType type);

    /**
     * Prettifies a field name without accessing configuration
     *
     * @param fieldPath any field path
     * @return field name with underscores replaced by spaces and words capitalised
     */
    String prettifyFieldName(String fieldPath);

    /**
     * Parses a display value from supplied field info for a given value
     *
     * @param getFieldInfo supplier for field information potentially containing value mapping
     * @param value field value
     * @param <T> field value type
     * @return the parsed display value
     */
    <T extends Serializable> String parseDisplayValue(Supplier<Optional<FieldInfo<? extends Serializable>>> getFieldInfo, T value);
}
