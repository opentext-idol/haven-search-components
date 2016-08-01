/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import org.joda.time.DateTime;

import java.util.function.Function;

public enum FieldType {
    STRING(String.class, value -> value),
    DATE(DateTime.class, value -> {
            try {
                final long epoch = Long.parseLong(value) * 1000;
                return new DateTime(epoch);
            } catch (final NumberFormatException ignore) {
                try {
                    // HOD handles date fields inconsistently; attempt to detect this here
                    return new DateTime(value);
                } catch (final IllegalArgumentException ignored) {
                    return null;
                }
            }
    }),
    NUMBER(Number.class, Double::parseDouble),
    BOOLEAN(Boolean.class, Boolean::parseBoolean);

    private final Class<?> type;
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final Function<String, ?> parser;

    FieldType(final Class<?> type, final Function<String, ?> parser) {
        this.type = type;
        this.parser = parser;
    }

    public Class<?> getType() {
        return type;
    }

    public <T> T parseValue(final Class<T> type, final String stringValue) {
        return type.cast(parser.apply(stringValue));
    }
}
