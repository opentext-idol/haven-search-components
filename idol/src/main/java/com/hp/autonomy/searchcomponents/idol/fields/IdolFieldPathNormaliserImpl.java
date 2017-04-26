/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser.FIELD_PATH_NORMALISER_BEAN_NAME;

/**
 * Default Idol implementation of {@link FieldPathNormaliser}
 */
@Component(FIELD_PATH_NORMALISER_BEAN_NAME)
class IdolFieldPathNormaliserImpl extends AbstractFieldPathNormaliser {
    private static final String IDX_PREFIX = "DOCUMENT/";
    private static final String XML_PREFIX = "DOCUMENTS/";
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("(/?[^/]+)+");
    private static final Pattern IDX_PATH_PATTERN = Pattern.compile("^/?(?:" + IDX_PREFIX + ")?(?<fieldPath>[^/]+)$");
    private static final Pattern XML_PATH_PATTERN = Pattern.compile("^/?(?:" + XML_PREFIX + ")?(?:" + IDX_PREFIX + ")?(?<fieldPath>[^/]+(?:/[^/]+)*)$");

    @Override
    public FieldPath normaliseFieldPath(final String fieldPath) {
        if (StringUtils.isBlank(fieldPath) || !FIELD_NAME_PATTERN.matcher(fieldPath.trim()).matches()) {
            throw new IllegalArgumentException("Field names may not be blank or contain only forward slashes");
        }

        String normalisedFieldName = fieldPath.toUpperCase();
        if (!ParametricValuesService.AUTN_DATE_FIELD.equals(normalisedFieldName)) {
            final Matcher idxMatcher = IDX_PATH_PATTERN.matcher(normalisedFieldName);
            if (idxMatcher.find()) {
                normalisedFieldName = idxMatcher.group("fieldPath");
            } else {
                final Matcher xmlMatcher = XML_PATH_PATTERN.matcher(normalisedFieldName);
                if (xmlMatcher.find()) {
                    normalisedFieldName = xmlMatcher.group("fieldPath");
                }
            }
        }

        final String fieldName = normalisedFieldName.contains("/") ? normalisedFieldName.substring(normalisedFieldName.lastIndexOf('/') + 1) : normalisedFieldName;
        return newFieldPath(normalisedFieldName, fieldName);
    }
}
