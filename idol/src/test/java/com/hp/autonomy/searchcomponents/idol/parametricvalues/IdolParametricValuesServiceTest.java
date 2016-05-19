/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.google.common.collect.ImmutableList;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.fields.IdolTagResponse;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.types.idol.FlatField;
import com.hp.autonomy.types.idol.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.GetTagNamesResponseData;
import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.idol.TagValue;
import com.hp.autonomy.types.idol.Values;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
        parametricValuesService = new IdolParametricValuesService(parameterHandler, fieldsService, contentAciService, aciResponseProcessorFactory);
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

        when(fieldsService.getParametricFields(any(IdolFieldsRequest.class))).thenReturn(Collections.singletonList("CATEGORY"));

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void parametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(new GetTagNamesResponseData());

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(empty()));
    }

    @Test
    public void getNumericParametricValues() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericField"));

        final GetQueryTagValuesResponseData responseData = mockNumericQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);
        final Set<QueryTagInfo> results = parametricValuesService.getNumericParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
        final Set<QueryTagCountInfo> countInfo = results.iterator().next().getValues();
        final Iterator<QueryTagCountInfo> iterator = countInfo.iterator();
        assertEquals(new QueryTagCountInfo("0.0", 3), iterator.next());
        assertEquals(new QueryTagCountInfo("4.0", 5), iterator.next());
        assertEquals(new QueryTagCountInfo("5.1", 1), iterator.next());
        assertEquals(new QueryTagCountInfo("12.0", 2), iterator.next());
    }

    @Test
    public void getNumericFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

        final IdolTagResponse tagResponse = IdolTagResponse.builder()
                .setNumericTypeFields(ImmutableList.of("NumericField", "ParametricNumericField"))
                .setParametricTypeFields(ImmutableList.of("ParametricField", "ParametricNumericField"))
                .build();
        when(fieldsService.getFields(any(IdolFieldsRequest.class), anyCollectionOf(String.class))).thenReturn(tagResponse);

        final GetQueryTagValuesResponseData responseData = mockNumericQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Set<QueryTagInfo> results = parametricValuesService.getNumericParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void numericParametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

        when(fieldsService.getFields(any(IdolFieldsRequest.class), anyCollectionOf(String.class))).thenReturn(IdolTagResponse.builder().build());
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(new GetTagNamesResponseData());

        final Set<QueryTagInfo> results = parametricValuesService.getNumericParametricValues(idolParametricRequest);
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

        when(fieldsService.getParametricFields(any(IdolFieldsRequest.class))).thenReturn(Collections.singletonList("CATEGORY"));

        final GetQueryTagValuesResponseData responseData = mockRecursiveResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void dependentParametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.<String>emptyList());

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
                .setSort(SortParam.DocumentCount.name())
                .setModified(true)
                .build();
    }

    private GetQueryTagValuesResponseData mockQueryResponse() {
        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final FlatField field = new FlatField();
        field.getName().add("Some name");
        when(element.getName()).thenReturn(new QName("", "value"));
        final TagValue tagValue = mockTagValue("Some field", 5);
        when(element.getValue()).thenReturn(tagValue);
        field.getValueOrSubvalueOrValues().add(element);
        responseData.getField().add(field);
        return responseData;
    }

    private GetQueryTagValuesResponseData mockNumericQueryResponse() {
        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final FlatField field = new FlatField();
        field.getName().add("ParametricNumericField");
        when(element.getName()).thenReturn(new QName("", "value"));
        final TagValue tagValue1 = mockTagValue("0", 1);
        final TagValue tagValue2 = mockTagValue("0, 4, 12", 2);
        final TagValue tagValue3 = mockTagValue("4", 3);
        final TagValue tagValue4 = mockTagValue("5.1", 1);
        when(element.getValue()).thenReturn(tagValue1).thenReturn(tagValue2).thenReturn(tagValue3).thenReturn(tagValue4);
        final List<JAXBElement<? extends Serializable>> values = field.getValueOrSubvalueOrValues();
        values.add(element);
        values.add(element);
        values.add(element);
        values.add(element);
        responseData.getField().add(field);
        return responseData;
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
