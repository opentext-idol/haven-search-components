/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representation of Idol Field Information
 * This object is both used inside configuration and as a request object.
 * Note that this object fulfills the lombok @Builder contract but does not use the annotation to avoid IntelliJ errors (https://github.com/mplushnikov/lombok-intellij-plugin/issues/127)
 *
 * @param <T> the field value type
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = FieldInfo.FieldInfoBuilder.class)
public class FieldInfo<T> implements RequestObject<FieldInfo<T>, FieldInfo.FieldInfoBuilder<T>> {
    private static final long serialVersionUID = -5649457890413743332L;

    private final String id;
    private final FieldType type;
    private final boolean advanced;
    @Singular
    private final Set<String> names;
    @SuppressWarnings({"NonSerializableFieldInSerializableClass", "MismatchedQueryAndUpdateOfCollection"})
    @Singular
    private final List<T> values;

    private FieldInfo(final FieldInfoBuilder<T> builder) {
        id = builder.id;
        type = builder.type;
        advanced = builder.advanced;
        names = builder.names;
        values = builder.values;
    }

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

    @Override
    public FieldInfoBuilder<T> toBuilder() {
        return new FieldInfoBuilder<>(this);
    }

    public static <T> FieldInfoBuilder<T> builder() {
        return new FieldInfoBuilder<>();
    }

    @SuppressWarnings({"WeakerAccess", "unused", "FieldMayBeFinal"})
    @Accessors(fluent = true)
    @Setter
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @JsonPOJOBuilder(withPrefix = "")
    public static class FieldInfoBuilder<T> implements RequestObjectBuilder<FieldInfo<T>, FieldInfoBuilder<T>> {
        private String id;
        private FieldType type = FieldType.STRING;
        private boolean advanced;
        private Set<String> names = new HashSet<>();
        private List<T> values = new ArrayList<>();

        private FieldInfoBuilder(final FieldInfo<T> fieldInfo) {
            id = fieldInfo.id;
            type = fieldInfo.type;
            advanced = fieldInfo.advanced;
            names = fieldInfo.names;
            values = fieldInfo.values;
        }

        @JsonProperty("type")
        public FieldInfoBuilder<T> setType(final String type) {
            this.type = type == null ? FieldType.STRING : FieldType.valueOf(type.toUpperCase());
            return this;
        }

        public FieldInfoBuilder<T> name(final String name) {
            names.add(name);
            return this;
        }

        public FieldInfoBuilder<T> names(final Collection<? extends String> names) {
            this.names.addAll(names);
            return this;
        }

        public FieldInfoBuilder<T> clearNames() {
            names.clear();
            return this;
        }

        public FieldInfoBuilder<T> value(final T value) {
            values.add(value);
            return this;
        }

        public FieldInfoBuilder<T> values(final Collection<? extends T> values) {
            this.values.addAll(values);
            return this;
        }

        public FieldInfoBuilder<T> clearValues() {
            values.clear();
            return this;
        }

        @Override
        public FieldInfo<T> build() {
            return new FieldInfo<>(this);
        }
    }
}
