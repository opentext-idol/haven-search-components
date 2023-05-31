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

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator.FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME;

/**
 * Default implementation of {@link FieldDisplayNameGenerator}.
 * Replaces underscores with spaces and capitalises the first letter of each word.
 */
@SuppressWarnings("unused")
@Component(FIELD_DISPLAY_NAME_GENERATOR_BEAN_NAME)
class FieldDisplayNameGeneratorImpl implements FieldDisplayNameGenerator {
    private final ConfigService<? extends HavenSearchCapable> configService;

    @Autowired
    public FieldDisplayNameGeneratorImpl(final ConfigService<? extends HavenSearchCapable> configService) {
        this.configService = configService;
    }

    @Override
    public String generateDisplayName(final FieldPath path) {
        final Optional<FieldInfo<? extends Serializable>> maybeFieldInfo = getFieldInfoFromConfigByName(path);
        return maybeFieldInfo
                .map(fieldInfo -> Optional.ofNullable(fieldInfo.getDisplayName())
                        .orElseGet(() -> defaultGenerateDisplayNameFromId(fieldInfo.getId())))
                .orElseGet(() -> defaultGenerateDisplayName(path));
    }

    @Override
    public String generateDisplayNameFromId(final String id) {
        final Optional<FieldInfo<? extends Serializable>> maybeFieldInfo = getFieldInfoFromConfigById(id);
        return maybeFieldInfo
                .flatMap(fieldInfo -> Optional.ofNullable(fieldInfo.getDisplayName()))
                .orElseGet(() -> defaultGenerateDisplayNameFromId(id));
    }

    @Override
    public <T extends Serializable> String generateDisplayValue(final FieldPath path, final T maybeValue, final FieldType fieldType) {
        return parseDisplayValue(() -> getFieldInfoFromConfigByName(path), maybeValue);

    }

    @Override
    public <T extends Serializable> String generateDisplayValueFromId(final String id, final T maybeValue, final FieldType fieldType) {
        return parseDisplayValue(() -> getFieldInfoFromConfigById(id), maybeValue);
    }

    @Override
    public <T extends Serializable> String parseDisplayValue(final Supplier<Optional<FieldInfo<? extends Serializable>>> getFieldInfo, final T maybeValue) {
        return Optional.ofNullable(maybeValue)
                .map(value -> getFieldInfo.get()
                        .flatMap(fieldInfo -> fieldInfo.getValues()
                                .stream()
                                .filter(fieldValue -> compareValues(value, fieldValue))
                                .findFirst()
                                .map(FieldValue::getDisplayValue))
                        .orElseGet(() -> defaultGenerateDisplayValue(value)))
                .orElse(null);
    }

    @Override
    public String prettifyFieldName(final String fieldPath) {
        final String fieldName = fieldPath.contains("/") ? fieldPath.substring(fieldPath.lastIndexOf('/') + 1) : fieldPath;
        return String.join(" ", Arrays.stream(StringUtils.split(fieldName, '_'))
                .filter(StringUtils::isNotBlank)
                .map(WordUtils::capitalizeFully)
                .collect(Collectors.toList()));
    }

    private <T extends Serializable> boolean compareValues(final T value, final FieldValue<?> fieldValue) {
        return String.valueOf(value).equalsIgnoreCase(String.valueOf(fieldValue.getValue()));
    }

    private Optional<FieldInfo<? extends Serializable>> getFieldInfoFromConfigById(final String id) {
        return Optional.ofNullable(configService.getConfig().getFieldsInfo().getFieldConfig().get(id));
    }

    private Optional<FieldInfo<? extends Serializable>> getFieldInfoFromConfigByName(final FieldPath path) {
        return Optional.ofNullable(configService.getConfig().getFieldsInfo().getFieldConfigByName().get(path));
    }

    private String defaultGenerateDisplayName(final FieldPath fieldPath) {
        final String normalisedFieldName = fieldPath.getNormalisedPath();
        return prettifyFieldName(normalisedFieldName);
    }

    private String defaultGenerateDisplayNameFromId(final String id) {
        return prettifyFieldName(id);
    }

    private <T extends Serializable> String defaultGenerateDisplayValue(final T value) {
        return String.valueOf(value);
    }
}
