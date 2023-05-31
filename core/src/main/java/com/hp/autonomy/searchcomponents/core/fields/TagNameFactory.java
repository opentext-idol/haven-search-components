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
