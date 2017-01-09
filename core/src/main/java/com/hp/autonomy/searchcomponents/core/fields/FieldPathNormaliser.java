/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

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
    String normaliseFieldPath(String fieldPath);
}
