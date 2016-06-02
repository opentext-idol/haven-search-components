/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AdaptiveBucketSizeEvaluatorFactoryImpl;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.types.idol.FlatField;
import com.hp.autonomy.types.idol.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.GetTagNamesResponseData;
import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.idol.TagValue;
import com.hp.autonomy.types.idol.Values;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolParametricValuesServiceTest {
    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private FieldsService<IdolFieldsRequest, AciErrorException> fieldsService;

    @Mock
    private AciService contentAciService;

    @Mock
    private AciResponseJaxbProcessorFactory aciResponseProcessorFactory;

    @Mock
    private JAXBElement<? extends Serializable> element;

    private IdolParametricValuesService parametricValuesService;

    @Before
    public void setUp() {
        parametricValuesService = new IdolParametricValuesService(parameterHandler, fieldsService, contentAciService, aciResponseProcessorFactory, new AdaptiveBucketSizeEvaluatorFactoryImpl());
    }

    @Test
    public void getAllParametricValues() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("Some field"));

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);
        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

        when(fieldsService.getFields(any(IdolFieldsRequest.class), eq(FieldTypeParam.Parametric))).thenReturn(ImmutableMap.of(FieldTypeParam.Parametric, Collections.singletonList("CATEGORY")));

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void parametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

        final Map<FieldTypeParam, List<String>> response = ImmutableMap.of(FieldTypeParam.Parametric, Collections.<String>emptyList());
        when(fieldsService.getFields(any(IdolFieldsRequest.class), any(FieldTypeParam.class))).thenReturn(response);
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(new GetTagNamesResponseData());

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(empty()));
    }

    @Test
    public void getNumericParametricValuesInBuckets() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericDateField"));
        mockBucketResponses(8, 1f, 21f, mockTagValue("1,6", 5), mockTagValue("6,11", 2), mockTagValue("21,", 1));
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, 5);
        assertThat(results, is(not(empty())));
        final RangeInfo info = results.iterator().next();
        final List<RangeInfo.Value> countInfo = info.getValues();
        assertEquals(8, info.getCount());
        assertEquals(1f, info.getMin(), 0);
        assertEquals(22f, info.getMax(), 0);
        final Iterator<RangeInfo.Value> iterator = countInfo.iterator();
        assertEquals(new RangeInfo.Value(5, 1, 6), iterator.next());
        assertEquals(new RangeInfo.Value(2, 6, 11), iterator.next());
        assertEquals(new RangeInfo.Value(0, 11, 16), iterator.next());
        assertEquals(new RangeInfo.Value(0, 16, 21), iterator.next());
        assertEquals(new RangeInfo.Value(1, 21, 22), iterator.next());
    }

    @Test
    public void bucketParametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, 35);
        assertThat(results, is(empty()));
    }

    @Test
    public void getDependentParametricValues() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("Some field"));

        final GetQueryTagValuesResponseData responseData = mockRecursiveResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);
        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getDependentValuesFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

        when(fieldsService.getFields(any(IdolFieldsRequest.class), eq(FieldTypeParam.Parametric))).thenReturn(ImmutableMap.of(FieldTypeParam.Parametric, Collections.singletonList("CATEGORY")));

        final GetQueryTagValuesResponseData responseData = mockRecursiveResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void dependentParametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

        final Map<FieldTypeParam, List<String>> response = ImmutableMap.of(FieldTypeParam.Parametric, Collections.<String>emptyList());
        when(fieldsService.getFields(any(IdolFieldsRequest.class), any(FieldTypeParam.class))).thenReturn(response);
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(new GetTagNamesResponseData());

        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(empty()));
    }

    private IdolParametricRequest mockRequest(final List<String> fieldNames) {
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("*").setFieldText("").setDatabases(Collections.<String>emptyList()).build();
        return new IdolParametricRequest.Builder()
                .setFieldNames(fieldNames)
                .setQueryRestrictions(queryRestrictions)
                .setMaxValues(30)
                .setSort(SortParam.DocumentCount)
                .setModified(true)
                .build();
    }

    private GetQueryTagValuesResponseData mockQueryResponse() {
        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final FlatField field = new FlatField();
        field.getName().add("Some name");
        when(element.getName()).thenReturn(new QName("", IdolParametricValuesService.VALUE_NODE_NAME));
        final TagValue tagValue = mockTagValue("Some field", 5);
        when(element.getValue()).thenReturn(tagValue);
        field.getValueAndSubvalueOrValues().add(element);
        responseData.getField().add(field);
        return responseData;
    }

    private void mockBucketResponses(final int count, final float min, final float max, final TagValue... tagValues) {
        when(element.getName()).thenReturn(new QName("", IdolParametricValuesService.VALUES_NODE_NAME), new QName("", IdolParametricValuesService.VALUE_MIN_NODE_NAME), new QName("", IdolParametricValuesService.VALUE_MAX_NODE_NAME), new QName("", IdolParametricValuesService.VALUE_NODE_NAME));
        OngoingStubbing<? extends Serializable> stub = when(element.getValue()).thenReturn(count).thenReturn(min).thenReturn(max);
        for (final TagValue tagValue : tagValues) {
            //noinspection unchecked,rawtypes
            stub = ((OngoingStubbing) stub).thenReturn(tagValue);
        }

        final GetQueryTagValuesResponseData responseData1 = new GetQueryTagValuesResponseData();
        final FlatField field1 = new FlatField();
        field1.getName().add("ParametricNumericDateField");
        field1.getValueAndSubvalueOrValues().add(element);
        field1.getValueAndSubvalueOrValues().add(element);
        field1.getValueAndSubvalueOrValues().add(element);
        responseData1.getField().add(field1);

        final GetQueryTagValuesResponseData responseData2 = new GetQueryTagValuesResponseData();
        final FlatField field2 = new FlatField();
        field2.getName().add("ParametricNumericDateField");
        for (final TagValue ignored : tagValues) {
            field2.getValueAndSubvalueOrValues().add(element);
        }
        responseData2.getField().add(field2);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData1).thenReturn(responseData2);
    }

    private TagValue mockTagValue(final String value, final int count) {
        final TagValue tagValue = new TagValue();
        tagValue.setValue(value);
        tagValue.setCount(count);
        return tagValue;
    }

    private GetQueryTagValuesResponseData mockRecursiveResponse() {
        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final RecursiveField field = new RecursiveField();
        field.setValue("Some field");
        field.setCount("5");
        final Values values = new Values();
        values.getField().add(field);
        responseData.setValues(values);

        return responseData;
    }
}
