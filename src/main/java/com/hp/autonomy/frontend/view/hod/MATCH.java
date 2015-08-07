/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.view.hod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 *
 */
//TODO This should be open sourced properly
class MATCH extends Specifier {

    public MATCH(final String field, final String value, final String... values) {
        this(Collections.singletonList(field), value, values);
    }

    public MATCH(final String field, final String[] values) {
        this(Collections.singletonList(field), values);
    }

    public MATCH(final String field, final Iterable<String> values) {
        this(Collections.singletonList(field), values);
    }

    public MATCH(final String[] fields, final String value, final String... values) {
        this(Arrays.asList(fields), value, values);
    }

    public MATCH(final String[] fields, final String[] values) {
        this(Arrays.asList(fields), values);
    }

    public MATCH(final String[] fields, final Iterable<String> values) {
        this(Arrays.asList(fields), values);
    }

    public MATCH(final Iterable<String> fields, final String value, final String... values) {
        this(fields, toList(value, values));
    }

    public MATCH(final Iterable<String> fields, final String[] values) {
        this(fields, Arrays.asList(values));
    }

    public MATCH(final Iterable<String> fields, final Iterable<String> values) {
        super("MATCH", fields, values);
        Validate.isTrue(!getValues().isEmpty(), "No values specified");
    }

    public static MATCH MATCH(final String field, final String value, final String... values) {
        return new MATCH(field, value, values);
    }

    public static MATCH MATCH(final String field, final String[] values) {
        return new MATCH(field, values);
    }

    public static MATCH MATCH(final String field, final Iterable<String> values) {
        return new MATCH(field, values);
    }

    public static MATCH MATCH(final String[] fields, final String value, final String... values) {
        return new MATCH(fields, value, values);
    }

    public static MATCH MATCH(final String[] fields, final String[] values) {
        return new MATCH(fields, values);
    }

    public static MATCH MATCH(final String[] fields, final Iterable<String> values) {
        return new MATCH(fields, values);
    }

    public static MATCH MATCH(final Iterable<String> fields, final String value, final String... values) {
        return new MATCH(fields, value, values);
    }

    public static MATCH MATCH(final Iterable<String> fields, final String[] values) {
        return new MATCH(fields, values);
    }

    public static MATCH MATCH(final Iterable<String> fields, final Iterable<String> values) {
        return new MATCH(fields, values);
    }

    private static <T> List<T> toList(final T first, final T... others) {
        final List<T> result = new ArrayList<>();
        result.add(first);
        result.addAll(Arrays.asList(others));

        return result;
    }
}
