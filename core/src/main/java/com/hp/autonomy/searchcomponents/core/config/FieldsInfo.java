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

package com.hp.autonomy.searchcomponents.core.config;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Configuration object containing a map of field information
 */
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "DefaultAnnotationParam", "CollectionDeclaredAsConcreteClass"})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonDeserialize(builder = FieldsInfo.FieldsInfoBuilder.class)
public class FieldsInfo extends SimpleComponent<FieldsInfo> implements Serializable {
    private static final long serialVersionUID = 7627012722603736269L;

    private final LinkedHashMap<String, FieldInfo<?>> fieldConfig;
    @JsonIgnore
    private final LinkedHashMap<FieldPath, FieldInfo<?>> fieldConfigByName;

    @JsonAnyGetter
    public LinkedHashMap<String, FieldInfo<?>> getFieldConfig() {
        return new LinkedHashMap<>(fieldConfig);
    }

    @SuppressWarnings({"WeakerAccess", "FieldMayBeFinal", "TypeMayBeWeakened", "CollectionDeclaredAsConcreteClass"})
    @JsonPOJOBuilder(withPrefix = "")
    public static class FieldsInfoBuilder {
        private LinkedHashMap<String, FieldInfo<?>> fieldConfig = new LinkedHashMap<>();
        private LinkedHashMap<FieldPath, FieldInfo<?>> fieldConfigByName = new LinkedHashMap<>();

        @JsonAnySetter
        public FieldsInfoBuilder populateResponseMap(final String key, final FieldInfo<?> value) {
            final FieldInfo<?> valueWithId = value.toBuilder().id(key).build();
            fieldConfig.put(key, valueWithId);
            valueWithId.getNames().forEach(name -> fieldConfigByName.put(name, valueWithId));
            return this;
        }
    }
}
