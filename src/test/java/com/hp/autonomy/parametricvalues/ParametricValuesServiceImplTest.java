/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.parametricvalues;

import com.google.common.collect.ImmutableSet;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
public class ParametricValuesServiceImplTest {

    @Test
    public void getsParametricValues() throws HodErrorException {
        final ParametricValuesService parametricValuesServiceImpl = new ParametricValuesServiceImpl(getParametricValuesService());

        final Set<ResourceIdentifier> indexes = ImmutableSet.<ResourceIdentifier>builder()
                .add(ResourceIdentifier.WIKI_ENG)
                .add(ResourceIdentifier.PATENTS)
                .build();

        final Set<String> fieldNames = ImmutableSet.<String>builder()
                .add("grassy field")
                .add("wasteland")
                .add("football field")
                .build();

        final ParametricRequest testRequest = new ParametricRequest.Builder()
                .setDatabases(indexes)
                .setFieldNames(fieldNames)
                .build();

        final Set<ParametricFieldName> fieldNamesSet = parametricValuesServiceImpl.getAllParametricValues(testRequest);

        final Map<String, ParametricFieldName> fieldNamesMap = new HashMap<>();

        for (final ParametricFieldName parametricFieldName : fieldNamesSet) {
            fieldNamesMap.put(parametricFieldName.getName(), parametricFieldName);
        }

        assertThat(fieldNamesMap, hasKey("grassy field"));
        assertThat(fieldNamesMap, hasKey("wasteland"));
        assertThat(fieldNamesMap, hasKey("football field"));

        assertThat(fieldNamesMap, not(hasKey("empty field")));

        final ParametricFieldName grassyField = fieldNamesMap.get("grassy field");

        assertThat(grassyField.getValues(), hasItem(new ParametricFieldName.SerializableValueAndCount("snakes", 33)));
    }

    @Test
    public void emptyFieldNamesReturnEmptyParametricValues() throws HodErrorException {
        final ParametricValuesService parametricValuesServiceImpl = new ParametricValuesServiceImpl(getParametricValuesService());

        final Set<ResourceIdentifier> indexes = ImmutableSet.<ResourceIdentifier>builder()
                .add(ResourceIdentifier.PATENTS)
                .build();

        final ParametricRequest testRequest = new ParametricRequest.Builder()
                .setDatabases(indexes)
                .setFieldNames(Collections.<String>emptySet())
                .build();

        final Set<ParametricFieldName> fieldNamesSet = parametricValuesServiceImpl.getAllParametricValues(testRequest);

        assertThat(fieldNamesSet, is(empty()));
    }

    private GetParametricValuesService getParametricValuesService() throws HodErrorException {
        final GetParametricValuesService getParametricValuesService = mock(GetParametricValuesService.class);

        final Map<String, Integer> fieldsOfFootball = new LinkedHashMap<>();
        fieldsOfFootball.put("worms", 100);
        fieldsOfFootball.put("slugs", 50);

        final Map<String, Integer> fieldsOfGrass = new LinkedHashMap<>();
        fieldsOfGrass.put("birds", 65);
        fieldsOfGrass.put("snakes", 33);

        final Map<String, Integer> fieldsOfWaste = new LinkedHashMap<>();
        fieldsOfWaste.put("humans", 153);
        fieldsOfWaste.put("mutants", 45);

        final FieldNames everythingFieldNames = new FieldNames.Builder()
                .addParametricValue("football field", fieldsOfFootball)
                .addParametricValue("grassy field", fieldsOfGrass)
                .addParametricValue("wasteland", fieldsOfWaste)
                .addParametricValue("empty field", new LinkedHashMap<String, Integer>())
                .build();

        //noinspection unchecked
        when(getParametricValuesService.getParametricValues(
                argThat(any(Collection.class)),
                argThat(any(Collection.class)),
                argThat(any(GetParametricValuesRequestBuilder.class))
        )).thenReturn(everythingFieldNames);

        return getParametricValuesService;
    }

}
