/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = FieldsInfo.Builder.class)
public class FieldsInfo implements Serializable {
    private static final long serialVersionUID = 7627012722603736269L;

    private Map<String, FieldInfo<?>> fieldConfig;
    private Map<String, FieldInfo<?>> fieldConfigByName;

    public FieldsInfo merge(final FieldsInfo other) {
        if (other != null) {
            if (fieldConfig == null) {
                fieldConfig = other.fieldConfig;
            } else {
                final Map<String, FieldInfo<?>> mergedCustomFields = new LinkedHashMap<>(other.fieldConfig);
                mergedCustomFields.putAll(fieldConfig);
                fieldConfig = mergedCustomFields;
            }
        }

        return this;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private final Map<String, FieldInfo<?>> fieldConfig = new LinkedHashMap<>();
        private final Map<String, FieldInfo<?>> fieldConfigByName = new LinkedHashMap<>();

        @JsonAnySetter
        public Builder populateResponseMap(final String key, final FieldInfo<?> value) {
            value.setId(key);
            fieldConfig.put(key, value);
            for (final String name : value.getNames()) {
                fieldConfigByName.put(name, value);
            }
            return this;
        }

        public FieldsInfo build() {
            return new FieldsInfo(fieldConfig, fieldConfigByName);
        }
    }
}
