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
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
public class FieldInfo<T extends Serializable> implements RequestObject<FieldInfo<T>, FieldInfo.FieldInfoBuilder<T>> {
    private static final long serialVersionUID = -5649457890413743332L;

    private final String id;
    private final FieldType type;
    private final boolean advanced;
    @Singular
    private final Set<FieldPath> names;
    private final String displayName;
    @SuppressWarnings({"NonSerializableFieldInSerializableClass", "MismatchedQueryAndUpdateOfCollection"})
    @Singular
    private final List<FieldValue<T>> values;

    // Array of potential <select> option values, or ['*'] for a edit box, or just [] for a non-editable field
    private final List<String> editable;

    private final Boolean csvExport;

    private FieldInfo(final FieldInfoBuilder<T> builder) {
        id = builder.id;
        type = builder.type;
        advanced = builder.advanced;
        names = builder.names;
        displayName = builder.displayName;
        values = builder.values;
        csvExport = builder.csvExport;
        editable = builder.editable;
    }

    public static <T extends Serializable> FieldInfoBuilder<T> builder() {
        return new FieldInfoBuilder<>();
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
    public Set<FieldPath> getNamesIfNotEmpty() {
        return names.isEmpty() ? null : names;
    }

    public List<FieldValue<T>> getValues() {
        return Collections.unmodifiableList(values);
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getEditable() {
        return Collections.unmodifiableList(editable);
    }

    @Override
    public FieldInfoBuilder<T> toBuilder() {
        return new FieldInfoBuilder<>(this);
    }

    @SuppressWarnings({"WeakerAccess", "unused", "FieldMayBeFinal"})
    @Accessors(fluent = true)
    @Setter
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @JsonPOJOBuilder(withPrefix = "")
    public static class FieldInfoBuilder<T extends Serializable> implements RequestObjectBuilder<FieldInfo<T>, FieldInfoBuilder<T>> {
        private String id;
        private FieldType type = FieldType.STRING;
        private boolean advanced;
        private Set<FieldPath> names = new HashSet<>();
        private String displayName;
        private List<FieldValue<T>> values = new ArrayList<>();
        private List<String> editable = new ArrayList<>();
        private Boolean csvExport;

        private FieldInfoBuilder(final FieldInfo<T> fieldInfo) {
            id = fieldInfo.id;
            type = fieldInfo.type;
            advanced = fieldInfo.advanced;
            names = fieldInfo.names;
            displayName = fieldInfo.displayName;
            values = fieldInfo.values;
            editable = fieldInfo.editable;
            csvExport = fieldInfo.csvExport;
        }

        @JsonProperty("type")
        public FieldInfoBuilder<T> setType(final String type) {
            this.type = type == null ? FieldType.STRING : FieldType.valueOf(type.toUpperCase());
            return this;
        }

        public FieldInfoBuilder<T> name(final FieldPath name) {
            names.add(name);
            return this;
        }

        public FieldInfoBuilder<T> names(final Collection<? extends FieldPath> names) {
            this.names.addAll(names);
            return this;
        }

        public FieldInfoBuilder<T> clearNames() {
            names.clear();
            return this;
        }

        public FieldInfoBuilder<T> value(final FieldValue<T> value) {
            values.add(value);
            return this;
        }

        public FieldInfoBuilder<T> values(final Collection<? extends FieldValue<T>> values) {
            this.values.addAll(values);
            return this;
        }

        public FieldInfoBuilder<T> editable(final Collection<? extends String> editable) {
            this.editable.addAll(editable);
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
