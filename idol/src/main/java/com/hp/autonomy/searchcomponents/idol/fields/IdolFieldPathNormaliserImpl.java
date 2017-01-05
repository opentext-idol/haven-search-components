/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser.FIELD_PATH_NORMALISER_BEAN_NAME;

/**
 * Default Idol implementation of {@link FieldPathNormaliser}
 */
@Component(FIELD_PATH_NORMALISER_BEAN_NAME)
class IdolFieldPathNormaliserImpl implements FieldPathNormaliser {
    private static final String FULL_PATH_IDENTIFIER = "DOCUMENT/";
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("(/?[^/]+)+");

    @Override
    public String normaliseFieldPath(final String fieldPath) {
        if (StringUtils.isBlank(fieldPath) || !FIELD_NAME_PATTERN.matcher(fieldPath.trim()).matches()) {
            throw new IllegalArgumentException("Field names may not be blank or contain only forward slashes");
        }

        String normalisedFieldName = fieldPath;
        if (fieldPath.contains(FULL_PATH_IDENTIFIER)) {
            if (!fieldPath.startsWith("/")) {
                normalisedFieldName = '/' + fieldPath;
            }
        } else if (!fieldPath.equals(ParametricValuesService.AUTN_DATE_FIELD)) {
            normalisedFieldName = '/' + FULL_PATH_IDENTIFIER + (fieldPath.startsWith("/") ? fieldPath.substring(1) : fieldPath);
        }

        return normalisedFieldName;
    }
}
