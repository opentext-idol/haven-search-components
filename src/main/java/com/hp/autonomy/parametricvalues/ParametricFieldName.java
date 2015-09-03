/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.parametricvalues;

import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
public class ParametricFieldName implements Serializable {
    private static final long serialVersionUID = 8684984220600356174L;

    private final String name;
    private final Set<SerializableValueAndCount> values;

    public ParametricFieldName(final String name, final Set<FieldNames.ValueAndCount> values) {
        this.name = name;

        final Set<SerializableValueAndCount> serializableValues = new HashSet<>();
        for(final FieldNames.ValueAndCount value : values) {
            serializableValues.add(new SerializableValueAndCount(value.getValue(), value.getCount()));
        }
        this.values = serializableValues;
    }

    @Data
    public static class SerializableValueAndCount implements Serializable {
        private static final long serialVersionUID = 170380467552241341L;

        private final String value;
        private final int count;
    }
}
