/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParams;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationConfig;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.apache.commons.lang3.NotImplementedException;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.class)
public class HodParametricValuesServiceTest {
    @Mock
    protected GetParametricValuesService getParametricValuesService;

    @Mock
    protected FieldsService<HodFieldsRequest, HodErrorException> fieldsService;

    @Mock
    protected ConfigService<HodSearchCapable> configService;

    @Mock
    protected AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;

    @Mock
    private HodAuthenticationPrincipal hodAuthenticationPrincipal;

    @Mock
    protected HodSearchCapable config;

    protected HodParametricValuesService parametricValuesService;

    @Before
    public void setUp() throws HodErrorException {
        parametricValuesService = new HodParametricValuesService(fieldsService, getParametricValuesService(), configService, authenticationInformationRetriever);
    }

    @Before
    public void mocks() throws HodErrorException {
        when(config.getQueryManipulation()).thenReturn(new QueryManipulationConfig("SomeProfile", "SomeIndex"));
        when(configService.getConfig()).thenReturn(config);

        when(hodAuthenticationPrincipal.getApplication()).thenReturn(new ResourceIdentifier("SomeDomain", "SomeIndex"));
        when(authenticationInformationRetriever.getPrincipal()).thenReturn(hodAuthenticationPrincipal);
    }

    @Test
    public void getsParametricValues() throws HodErrorException {
        final List<ResourceIdentifier> indexes = Arrays.asList(ResourceIdentifier.WIKI_ENG, ResourceIdentifier.PATENTS);

        final List<String> fieldNames = ImmutableList.<String>builder()
                .add("grassy field")
                .add("wasteland")
                .add("football field")
                .build();

        final HodParametricRequest testRequest = generateRequest(indexes, fieldNames);

        final Set<QueryTagInfo> fieldNamesSet = parametricValuesService.getAllParametricValues(testRequest);

        final Map<String, QueryTagInfo> fieldNamesMap = new HashMap<>();

        for (final QueryTagInfo parametricFieldName : fieldNamesSet) {
            fieldNamesMap.put(parametricFieldName.getName(), parametricFieldName);
        }

        assertThat(fieldNamesMap, hasKey("grassy field"));
        assertThat(fieldNamesMap, hasKey("wasteland"));
        assertThat(fieldNamesMap, hasKey("football field"));

        assertThat(fieldNamesMap, not(hasKey("empty field")));

        final QueryTagInfo grassyField = fieldNamesMap.get("grassy field");

        assertThat(grassyField.getValues(), hasItem(new QueryTagCountInfo("snakes", 33)));
    }

    @Test
    public void emptyFieldNamesReturnEmptyParametricValues() throws HodErrorException {
        final Map<FieldTypeParam, List<TagName>> response = ImmutableMap.of(FieldTypeParam.Parametric, Collections.<TagName>emptyList());
        when(fieldsService.getFields(any(HodFieldsRequest.class), any(FieldTypeParam.class))).thenReturn(response);

        final List<ResourceIdentifier> indexes = Collections.singletonList(ResourceIdentifier.PATENTS);
        final HodParametricRequest testRequest = generateRequest(indexes, Collections.emptyList());
        final Set<QueryTagInfo> fieldNamesSet = parametricValuesService.getAllParametricValues(testRequest);
        assertThat(fieldNamesSet, is(empty()));
    }

    @Test
    public void lookupFieldNames() throws HodErrorException {
        when(fieldsService.getFields(any(HodFieldsRequest.class), eq(FieldTypeParam.Parametric))).thenReturn(ImmutableMap.of(FieldTypeParam.Parametric, Collections.singletonList(new TagName("grassy field"))));

        final List<ResourceIdentifier> indexes = Collections.singletonList(ResourceIdentifier.WIKI_ENG);
        final HodParametricRequest testRequest = generateRequest(indexes, Collections.emptyList());
        final Set<QueryTagInfo> fieldNamesSet = parametricValuesService.getAllParametricValues(testRequest);
        assertThat(fieldNamesSet, is(not(empty())));
    }

    @Test
    public void getValueDetailsNoFields() throws HodErrorException {
        final HodParametricRequest parametricRequest = generateRequest(Collections.singletonList(ResourceIdentifier.WIKI_ENG), Collections.emptyList());
        assertThat(parametricValuesService.getValueDetails(parametricRequest).size(), is(0));
    }

    @Test
    public void getValueDetails() throws HodErrorException {
        final String field = "MyField";
        final HodParametricRequest parametricRequest = generateRequest(Collections.singletonList(ResourceIdentifier.WIKI_ENG), Collections.singletonList(field));

        final FieldNames response = mockNumericQueryResponse(field, ImmutableMap.of("1", 1, "0.8,4.2", 2, "1,9", 1, "12", 1, "21", 1));
        when(getParametricValuesService.getParametricValues(anyCollectionOf(String.class), anyCollectionOf(ResourceIdentifier.class), any(GetParametricValuesRequestBuilder.class))).thenReturn(response);

        final Map<TagName, ValueDetails> valueDetails = parametricValuesService.getValueDetails(parametricRequest);
        assertThat(valueDetails.size(), is(1));
        assertThat(valueDetails, hasEntry(new TagName(field), new ValueDetails(0.8, 21, 6, 54, 6)));
    }

    @Test
    public void getNumericParametricValuesInBuckets() throws HodErrorException {
        final FieldNames responseData = mockNumericQueryResponse("ParametricNumericDateField", ImmutableMap.of("1", 1, "1,3", 2, "6,11", 1, "12", 1, "21", 1));
        when(getParametricValuesService.getParametricValues(anyCollectionOf(String.class), anyCollectionOf(ResourceIdentifier.class), any(GetParametricValuesRequestBuilder.class))).thenReturn(responseData);

        final HodParametricRequest hodParametricRequest = generateRequest(Collections.singletonList(ResourceIdentifier.WIKI_ENG), Collections.singletonList("ParametricNumericDateField"));
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(hodParametricRequest, ImmutableMap.of("ParametricNumericDateField", new BucketingParams(9, 3.0, 12.0)));

        assertThat(results, hasSize(1));

        final RangeInfo info = results.iterator().next();
        assertThat(info.getCount(), is(4));
        assertThat(info.getMin(), is(3d));
        assertThat(info.getMax(), is(12d));

        final Iterator<RangeInfo.Value> iterator = info.getValues().iterator();
        assertEquals(new RangeInfo.Value(2, 3, 4), iterator.next());
        assertEquals(new RangeInfo.Value(0, 4, 5), iterator.next());
        assertEquals(new RangeInfo.Value(0, 5, 6), iterator.next());
        assertEquals(new RangeInfo.Value(1, 6, 7), iterator.next());
        assertEquals(new RangeInfo.Value(0, 7, 8), iterator.next());
        assertEquals(new RangeInfo.Value(0, 8, 9), iterator.next());
        assertEquals(new RangeInfo.Value(0, 9, 10), iterator.next());
        assertEquals(new RangeInfo.Value(0, 10, 11), iterator.next());
        assertEquals(new RangeInfo.Value(1, 11, 12), iterator.next());
    }

    @Test
    public void getNumericParametricValuesInBucketsNoResults() throws HodErrorException {
        final FieldNames responseData = mockNumericQueryResponse("ParametricNumericDateField", ImmutableMap.of("1", 1, "1,3", 2, "6,9", 1, "12", 1, "21", 1));
        when(getParametricValuesService.getParametricValues(anyCollectionOf(String.class), anyCollectionOf(ResourceIdentifier.class), any(GetParametricValuesRequestBuilder.class))).thenReturn(responseData);

        final HodParametricRequest hodParametricRequest = generateRequest(Collections.singletonList(ResourceIdentifier.WIKI_ENG), Collections.singletonList("ParametricNumericDateField"));
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(hodParametricRequest, ImmutableMap.of("ParametricNumericDateField", new BucketingParams(5, 10.0, 11.0)));

        assertThat(results, is(not(empty())));

        final RangeInfo info = results.iterator().next();
        assertEquals(0, info.getCount());
        assertEquals(10d, info.getMin(), 0);
        assertEquals(11d, info.getMax(), 0);

        final Iterator<RangeInfo.Value> iterator = info.getValues().iterator();
        assertEquals(new RangeInfo.Value(0, 10, 10.2), iterator.next());
        assertEquals(new RangeInfo.Value(0, 10.2, 10.4), iterator.next());
        assertEquals(new RangeInfo.Value(0, 10.4, 10.6), iterator.next());
        assertEquals(new RangeInfo.Value(0, 10.6, 10.8), iterator.next());
        assertEquals(new RangeInfo.Value(0, 10.8, 11), iterator.next());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNumericParametricValuesZeroBucketsZeroBuckets() throws HodErrorException {
        final HodParametricRequest hodParametricRequest = generateRequest(Collections.singletonList(ResourceIdentifier.WIKI_ENG), Collections.singletonList("ParametricNumericDateField"));
        parametricValuesService.getNumericParametricValuesInBuckets(hodParametricRequest, ImmutableMap.of("ParametricNumericDateField", new BucketingParams(0, 10.0, 11.0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNumericParametricValuesNoParams() throws HodErrorException {
        final HodParametricRequest hodParametricRequest = generateRequest(Collections.singletonList(ResourceIdentifier.WIKI_ENG), Collections.singletonList("ParametricNumericDateField"));
        parametricValuesService.getNumericParametricValuesInBuckets(hodParametricRequest, Collections.emptyMap());
    }

    @Test
    public void getNumericParametricValuesInBucketsNoFields() throws HodErrorException {
        final HodParametricRequest hodParametricRequest = generateRequest(Collections.singletonList(ResourceIdentifier.WIKI_ENG), Collections.singletonList("ParametricNumericDateField"));
        final FieldNames responseData = new FieldNames.Builder()
                .addParametricValue("numericParametricValue", Collections.emptyMap())
                .build();
        when(getParametricValuesService.getParametricValues(anyCollectionOf(String.class), anyCollectionOf(ResourceIdentifier.class), any(GetParametricValuesRequestBuilder.class))).thenReturn(responseData);
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(hodParametricRequest, ImmutableMap.of("ParametricNumericDateField", new BucketingParams(3, 1.5, 5.5)));
        MatcherAssert.assertThat(results, empty());
    }

    @Test(expected = NotImplementedException.class)
    public void dependentParametricValues() throws HodErrorException {
        parametricValuesService.getDependentParametricValues(new HodParametricRequest.Builder().build());
    }

    private HodParametricRequest generateRequest(final List<ResourceIdentifier> indexes, final List<String> fieldNames) {
        final QueryRestrictions<ResourceIdentifier> queryRestrictions = new HodQueryRestrictions.Builder().setDatabases(indexes).build();
        return new HodParametricRequest.Builder()
                .setFieldNames(fieldNames)
                .setQueryRestrictions(queryRestrictions)
                .build();
    }

    private FieldNames mockNumericQueryResponse(final String fieldName, final Map<String, Integer> map) {
        return new FieldNames.Builder()
                .addParametricValue(fieldName, map)
                .build();
    }

    protected GetParametricValuesService getParametricValuesService() throws HodErrorException {
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
                .addParametricValue("empty field", new LinkedHashMap<>())
                .build();

        when(getParametricValuesService.getParametricValues(anyCollectionOf(String.class), anyCollectionOf(ResourceIdentifier.class), any(GetParametricValuesRequestBuilder.class)
        )).thenReturn(everythingFieldNames);

        return getParametricValuesService;
    }

}
