/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * The different possible Idol field value types and how to convert them to {@link String}
 */
public enum FieldType {
    STRING(String.class, value -> value),
    DATE(ZonedDateTime.class, value -> {
            try {
                final long epoch = Long.parseLong(value);
                return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneOffset.UTC);
            } catch (final NumberFormatException ignore) {
                try {
                    // HOD handles date fields inconsistently; attempt to detect this here
                    return ZonedDateTime.parse(value);
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
