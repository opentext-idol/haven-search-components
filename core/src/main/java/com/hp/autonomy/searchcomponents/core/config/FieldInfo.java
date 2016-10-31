/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = FieldInfo.FieldInfoBuilder.class)
public class FieldInfo<T> implements Serializable {
    private static final long serialVersionUID = -5649457890413743332L;

    private final String id;
    private final FieldType type;
    private final boolean advanced;
    @Singular
    private final Set<String> names;
    @SuppressWarnings({"NonSerializableFieldInSerializableClass", "MismatchedQueryAndUpdateOfCollection"})
    @Singular
    private final List<T> values;

    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getTypeAsString() {
        return type == FieldType.STRING ? null : type.name().toLowerCase();
    }

    @SuppressWarnings("unused")
    public Boolean isAdvancedIfNotDefault() {
        return advanced ? true : null;
    }

    @SuppressWarnings("unused")
    public Set<String> getNamesIfNotEmpty() {
        return names.isEmpty() ? null : names;
    }

    public List<T> getValues() {
        return Collections.unmodifiableList(values);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    public static class FieldInfoBuilder<T> {
        private FieldType type = FieldType.STRING;

        @JsonProperty("type")
        public FieldInfoBuilder<T> setType(final String type) {
            this.type = type == null ? FieldType.STRING : FieldType.valueOf(type.toUpperCase());
            return this;
        }
    }
}
