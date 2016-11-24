/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciParameter;
import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParams;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParamsHelper;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.FlatField;
import com.hp.autonomy.types.idol.responses.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.responses.GetTagNamesResponseData;
import com.hp.autonomy.types.idol.responses.RecursiveField;
import com.hp.autonomy.types.idol.responses.TagValue;
import com.hp.autonomy.types.idol.responses.Values;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = CoreTestContext.class, properties = CORE_CLASSES_PROPERTY)
public class IdolParametricValuesServiceTest {
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private FieldsService<IdolFieldsRequest, AciErrorException> fieldsService;

    @Autowired
    private BucketingParamsHelper bucketingParamsHelper;

    @Mock
    private AciService contentAciService;

    @Mock
    private AciServiceRetriever aciServiceRetriever;

    @Mock
    private ProcessorFactory aciResponseProcessorFactory;

    @Mock
    private JAXBElement<Serializable> element;

    private IdolParametricValuesService parametricValuesService;

    @SuppressWarnings("CastToConcreteClass")
    @Before
    public void setUp() {
        parametricValuesService = new IdolParametricValuesServiceImpl(parameterHandler, fieldsService, bucketingParamsHelper, aciServiceRetriever, aciResponseProcessorFactory);

        when(aciServiceRetriever.getAciService(any(QueryRequest.QueryType.class))).thenReturn(contentAciService);
    }

    @Test
    public void getAllParametricValues() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("Some field"));

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);
        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        when(fieldsService.getFields(any(IdolFieldsRequest.class), eq(FieldTypeParam.Parametric))).thenReturn(ImmutableMap.of(FieldTypeParam.Parametric, Collections.singletonList(new TagName("CATEGORY"))));

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void parametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        final Map<FieldTypeParam, List<TagName>> response = ImmutableMap.of(FieldTypeParam.Parametric, Collections.emptyList());
        when(fieldsService.getFields(any(IdolFieldsRequest.class), any(FieldTypeParam.class))).thenReturn(response);
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(new GetTagNamesResponseData());

        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(idolParametricRequest);
        assertThat(results, is(empty()));
    }

    @Test
    public void getValueDetailsNoFields() {
        final IdolParametricRequest parametricRequest = mockRequest(Collections.emptyList());
        assertThat(parametricValuesService.getValueDetails(parametricRequest).size(), is(0));
    }

    @Test
    public void getValueDetails() {
        final String elevation = "DOCUMENT/ELEVATION";
        final String age = "DOCUMENT/AGE";
        final String notThere = "DOCUMENT/NOT_THERE";
        final IdolParametricRequest parametricRequest = mockRequest(Arrays.asList(elevation, age, notThere));

        final List<FlatField> responseFields = new LinkedList<>();
        responseFields.add(mockFlatField(elevation, -50, 1242, 12314, 500.5, 3));
        responseFields.add(mockFlatField(age, 0, 96, 1314, 26, 100));
        responseFields.add(mockFlatField(notThere, 0, 0, 0, 0, null));

        final GetQueryTagValuesResponseData response = mock(GetQueryTagValuesResponseData.class);
        when(response.getField()).thenReturn(responseFields);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(response);

        final Map<TagName, ValueDetails> valueDetails = parametricValuesService.getValueDetails(parametricRequest);
        assertThat(valueDetails.size(), is(3));
        assertThat(valueDetails, hasEntry(equalTo(new TagName(elevation)), equalTo(new ValueDetails(-50, 1242, 500.5, 12314, 3))));
        assertThat(valueDetails, hasEntry(equalTo(new TagName(age)), equalTo(new ValueDetails(0, 96, 26, 1314, 100))));
        assertThat(valueDetails, hasEntry(equalTo(new TagName(notThere)), equalTo(new ValueDetails(0d, 0d, 0d, 0d, 0))));
    }

    @Test
    public void getNumericParametricValues() {
        mockBucketResponses(7, mockTagValue("2,3", 5), mockTagValue("3,4", 2));
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericDateField"));
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, ImmutableMap.of("ParametricNumericDateField", new BucketingParams(5, 1.0, 6.0)));
        assertThat(results, is(not(empty())));
        final RangeInfo info = results.iterator().next();
        final List<RangeInfo.Value> countInfo = info.getValues();
        assertEquals(7, info.getCount());
        assertEquals(1, info.getMin(), 0);
        assertEquals(6d, info.getMax(), 0);
        final Iterator<RangeInfo.Value> iterator = countInfo.iterator();
        assertEquals(new RangeInfo.Value(0, 1, 2), iterator.next());
        assertEquals(new RangeInfo.Value(5, 2, 3), iterator.next());
        assertEquals(new RangeInfo.Value(2, 3, 4), iterator.next());
        assertEquals(new RangeInfo.Value(0, 4, 5), iterator.next());
        assertEquals(new RangeInfo.Value(0, 5, 6), iterator.next());
    }

    @Test
    public void getNumericParametricValuesInBucketsNoResults() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericDateField"));
        mockBucketResponses(0);
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, ImmutableMap.of("ParametricNumericDateField", new BucketingParams(5, 1.0, 6.0)));
        assertThat(results, is(not(empty())));
        final RangeInfo info = results.iterator().next();
        final List<RangeInfo.Value> countInfo = info.getValues();
        assertEquals(0, info.getCount());
        assertEquals(1, info.getMin(), 0);
        assertEquals(6d, info.getMax(), 0);
        final Iterator<RangeInfo.Value> iterator = countInfo.iterator();
        assertEquals(new RangeInfo.Value(0, 1, 2), iterator.next());
        assertEquals(new RangeInfo.Value(0, 2, 3), iterator.next());
        assertEquals(new RangeInfo.Value(0, 3, 4), iterator.next());
        assertEquals(new RangeInfo.Value(0, 4, 5), iterator.next());
        assertEquals(new RangeInfo.Value(0, 5, 6), iterator.next());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNumericParametricValuesZeroBucketsZeroBuckets() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericDateField"));
        parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, ImmutableMap.of("ParametricNumericDateField", new BucketingParams(0, 1.0, 5.0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNumericParametricValuesNoParams() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericDateField"));
        parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, Collections.emptyMap());
    }

    @Test
    public void bucketParametricValuesNoFields() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, Collections.emptyMap());
        assertThat(results, is(empty()));
    }

    @Test
    public void getDependentParametricValues() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("Some field"));

        final GetQueryTagValuesResponseData responseData = mockRecursiveResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);
        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getDependentValuesFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        when(fieldsService.getFields(any(IdolFieldsRequest.class), eq(FieldTypeParam.Parametric))).thenReturn(ImmutableMap.of(FieldTypeParam.Parametric, Collections.singletonList(new TagName("CATEGORY"))));

        final GetQueryTagValuesResponseData responseData = mockRecursiveResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void dependentParametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        final Map<FieldTypeParam, List<TagName>> response = ImmutableMap.of(FieldTypeParam.Parametric, Collections.emptyList());
        when(fieldsService.getFields(any(IdolFieldsRequest.class), any(FieldTypeParam.class))).thenReturn(response);
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(new GetTagNamesResponseData());

        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(empty()));
    }

    private IdolParametricRequest mockRequest(final Collection<String> fieldNames) {
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder()
                .queryText("*")
                .fieldText("")
                .databases(Collections.emptyList())
                .build();
        return IdolParametricRequest.builder()
                .fieldNames(fieldNames)
                .queryRestrictions(queryRestrictions)
                .maxValues(30)
                .sort(SortParam.DocumentCount)
                .modified(true)
                .build();
    }

    private GetQueryTagValuesResponseData mockQueryResponse() {
        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final FlatField field = new FlatField();
        field.getName().add("Some name");
        when(element.getName()).thenReturn(new QName("", IdolParametricValuesServiceImpl.VALUE_NODE_NAME));
        final TagValue tagValue = mockTagValue("Some field", 5);
        when(element.getValue()).thenReturn(tagValue);
        field.getValueAndSubvalueOrValues().add(element);
        responseData.getField().add(field);
        return responseData;
    }

    private JAXBElement<Serializable> mockJAXBElement(final String name, final double value) {
        @SuppressWarnings("unchecked")
        final JAXBElement<Serializable> jaxbElement = mock(JAXBElement.class);

        when(jaxbElement.getName()).thenReturn(new QName("", name));
        when(jaxbElement.getValue()).thenReturn(value);
        return jaxbElement;
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private FlatField mockFlatField(final String name, final double min, final double max, final double sum, final double average, final Integer totalValues) {
        final FlatField flatField = mock(FlatField.class);
        when(flatField.getName()).thenReturn(Collections.singletonList(name));
        when(flatField.getTotalValues()).thenReturn(totalValues);

        final List<JAXBElement<? extends Serializable>> values = new LinkedList<>();
        values.add(mockJAXBElement(IdolParametricValuesServiceImpl.VALUE_MIN_NODE_NAME, min));
        values.add(mockJAXBElement(IdolParametricValuesServiceImpl.VALUE_MAX_NODE_NAME, max));
        values.add(mockJAXBElement(IdolParametricValuesServiceImpl.VALUE_SUM_NODE_NAME, sum));
        values.add(mockJAXBElement(IdolParametricValuesServiceImpl.VALUE_AVERAGE_NODE_NAME, average));

        when(flatField.getValueAndSubvalueOrValues()).thenReturn(values);
        return flatField;
    }

    private void mockBucketResponses(final int count, final TagValue... tagValues) {
        when(element.getName()).thenReturn(
                new QName("", IdolParametricValuesServiceImpl.VALUES_NODE_NAME),
                new QName("", IdolParametricValuesServiceImpl.VALUE_NODE_NAME)
        );

        OngoingStubbing<Serializable> stub = when(element.getValue()).thenReturn(count);

        for (final TagValue tagValue : tagValues) {
            stub = stub.thenReturn(tagValue);
        }

        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final FlatField field2 = new FlatField();
        field2.getName().add("ParametricNumericDateField");
        field2.getValueAndSubvalueOrValues().add(element);
        for (final TagValue ignored : tagValues) {
            field2.getValueAndSubvalueOrValues().add(element);
        }
        responseData.getField().add(field2);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);
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
