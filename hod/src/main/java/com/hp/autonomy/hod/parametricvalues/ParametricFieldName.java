/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.hod.parametricvalues;

import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ParametricFieldName implements QueryTagInfo<FieldNames.ValueAndCount> {
    private static final long serialVersionUID = 8684984220600356174L;

    private final String name;
    private final Set<FieldNames.ValueAndCount> values;

    public ParametricFieldName(final String name, final Set<FieldNames.ValueAndCount> values) {
        this.name = name;
        this.values = new HashSet<>(values);
    }
}
