/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import org.springframework.stereotype.Component;

import static com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser.FIELD_PATH_NORMALISER_BEAN_NAME;

/**
 * Default HoD implementation of {@link FieldPathNormaliser}.
 * There is no concept of field path normalisation in HoD.
 */
@Component(FIELD_PATH_NORMALISER_BEAN_NAME)
class HodFieldPathNormaliserImpl implements FieldPathNormaliser {
    @Override
    public String normaliseFieldPath(final String fieldPath) {
        return fieldPath;
    }
}
