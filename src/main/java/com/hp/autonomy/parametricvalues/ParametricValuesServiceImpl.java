/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.parametricvalues;

import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.ParametricSort;
import com.hp.autonomy.hod.client.error.HodErrorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ParametricValuesServiceImpl implements ParametricValuesService {

    private final GetParametricValuesService getParametricValuesService;

    public ParametricValuesServiceImpl(final GetParametricValuesService getParametricValuesService) {
        this.getParametricValuesService = getParametricValuesService;
    }

    @Override
    public Set<ParametricFieldName> getAllParametricValues(final ParametricRequest parametricRequest) throws HodErrorException {
        final GetParametricValuesRequestBuilder parametricParams = new GetParametricValuesRequestBuilder()
                .setSort(ParametricSort.document_count)
                .setText(parametricRequest.getQuery())
                .setFieldText(parametricRequest.getFieldText())
                .setMaxValues(5);

        final FieldNames fieldNames = getParametricValuesService.getParametricValues(parametricRequest.getFieldNames(),
                new ArrayList<>(parametricRequest.getDatabases()), parametricParams);
        final Set<String> fieldNamesSet = fieldNames.getFieldNames();
        final Set<ParametricFieldName> parametricFieldNames = new HashSet<>();

        for (final String name : fieldNamesSet) {
            final Set<FieldNames.ValueAndCount> values = new HashSet<>(fieldNames.getValuesAndCountsForFieldName(name));
            if(!values.isEmpty()) {
                parametricFieldNames.add(new ParametricFieldName(name, values));
            }
        }

        return parametricFieldNames;
    }

}
