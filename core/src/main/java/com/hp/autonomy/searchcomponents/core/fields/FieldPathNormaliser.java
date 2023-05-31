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
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Handles normalisation of field paths (e.g. as supplied by Idol responses or in a user's application configuration)
 */
@FunctionalInterface
public interface FieldPathNormaliser {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String FIELD_PATH_NORMALISER_BEAN_NAME = "fieldPathNormaliser";

    /**
     * Normalises Idol field paths
     * Accepts /DOCUMENT/FOO, DOCUMENT/FOO, /FOO, FOO, /DOCUMENTS/DOCUMENT/FOO/BAR, DOCUMENTS/DOCUMENT/FOO/BAR
     * Returns /DOCUMENT/FOO or /DOCUMENTS/DOCUMENT/FOO/BAR accordingly
     *
     * @param fieldPath the field path to normalise
     * @return the normalised path
     */
    FieldPath normaliseFieldPath(String fieldPath);
}
