/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Generates a tag name object from a field path, handling any necessary normalisation/prettification
 */
public interface TagNameFactory {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String TAG_NAME_FACTORY_BEAN_NAME = "tagNameFactory";

    /**
     * Generates a tagName object
     *
     * @param path a path (see {@link FieldPathNormaliser} for supported values)
     * @return a tag name object with a unique path id and a prettified display name
     */
    TagName buildTagName(String path);

    /**
     * Generates a field path object
     *
     * @param path any form of path
     * @return a field path
     */
    FieldPath getFieldPath(String path);

    /**
     * Retrieves a display value for the supplied tag value
     *
     * @param path a field path (see {@link FieldPathNormaliser} for supported values)
     * @param value any String value
     * @return a display value
     */
    String getTagDisplayValue(String path, String value);
}
