/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator.FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME;

/**
 * Default implementation of {@link FieldDisplayNameGenerator}.
 * Replaces underscores with spaces and capitalises the first letter of each word.
 */
@Component(FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME)
class FieldDisplayNameGeneratorImpl implements FieldDisplayNameGenerator {
    @Override
    public String generateDisplayName(final String normalisedFieldName) {
        final String fieldName = normalisedFieldName.contains("/") ? normalisedFieldName.substring(normalisedFieldName.lastIndexOf('/') + 1) : normalisedFieldName;
        return String.join(" ", Arrays.stream(StringUtils.split(fieldName, '_'))
                .filter(StringUtils::isNotBlank)
                .map(WordUtils::capitalizeFully)
                .collect(Collectors.toList()));
    }
}
