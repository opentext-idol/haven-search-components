/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import org.joda.time.DateTime;

public enum FieldType {
    STRING(String.class, new FieldValueParser<String>() {
        @Override
        public String parse(final String value) {
            return value;
        }
    }),
    DATE(DateTime.class, new FieldValueParser<DateTime>() {
        @Override
        public DateTime parse(final String value) {
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
        }
    }),
    NUMBER(Number.class, new FieldValueParser<Number>() {
        @Override
        public Number parse(final String value) {
            return Double.parseDouble(value);
        }
    }),
    BOOLEAN(Boolean.class, new FieldValueParser<Boolean>() {
        @Override
        public Boolean parse(final String value) {
            return Boolean.parseBoolean(value);
        }
    });

    private final Class<?> type;
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final FieldValueParser<?> parser;

    FieldType(final Class<?> type, final FieldValueParser<?> parser) {
        this.type = type;
        this.parser = parser;
    }

    public Class<?> getType() {
        return type;
    }

    public <T> T parseValue(final Class<T> type, final String stringValue) {
        return type.cast(parser.parse(stringValue));
    }
}
