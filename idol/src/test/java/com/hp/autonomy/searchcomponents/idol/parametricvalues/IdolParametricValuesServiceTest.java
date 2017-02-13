/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.util.AciParameters;
import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParams;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParamsHelper;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsService;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.QueryExecutor;
import com.hp.autonomy.types.idol.responses.*;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
    private IdolFieldsService fieldsService;

    @Mock
    private ObjectFactory<IdolFieldsRequestBuilder> fieldsRequestBuilderFactory;

    @Mock
    private IdolFieldsRequestBuilder fieldsRequestBuilder;

    @Autowired
    private BucketingParamsHelper bucketingParamsHelper;

    @Autowired
    private TagNameFactory tagNameFactory;

    @Mock
    private JAXBElement<Serializable> element;

    @Mock
    private QueryExecutor queryExecutor;

    private IdolParametricValuesService parametricValuesService;

    @SuppressWarnings("CastToConcreteClass")
    @Before
    public void setUp() {
        when(fieldsRequestBuilderFactory.getObject()).thenReturn(fieldsRequestBuilder);

        parametricValuesService = new IdolParametricValuesServiceImpl(
                parameterHandler,
                fieldsService,
                fieldsRequestBuilderFactory,
                bucketingParamsHelper,
                tagNameFactory,
                queryExecutor
        );
    }

    @Test
    public void getParametricValues() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("Some field"));

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(queryExecutor.executeGetQueryTagValues(any(AciParameters.class), any())).thenReturn(responseData);
        final Set<QueryTagInfo> results = parametricValuesService.getParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        final ImmutableMap<FieldTypeParam, List<TagName>> fieldsResponse = ImmutableMap.of(FieldTypeParam.Parametric, Collections.singletonList(tagNameFactory.buildTagName("CATEGORY")));
        when(fieldsService.getFields(any(), eq(FieldTypeParam.Parametric))).thenReturn(fieldsResponse);

        final GetQueryTagValuesResponseData responseData = mockQueryResponse();
        when(queryExecutor.executeGetQueryTagValues(any(AciParameters.class), any())).thenReturn(responseData);

        final Set<QueryTagInfo> results = parametricValuesService.getParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void parametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        final Map<FieldTypeParam, List<TagName>> response = ImmutableMap.of(FieldTypeParam.Parametric, Collections.emptyList());
        when(fieldsService.getFields(any(), any(FieldTypeParam.class))).thenReturn(response);

        final Set<QueryTagInfo> results = parametricValuesService.getParametricValues(idolParametricRequest);
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

        when(queryExecutor.executeGetQueryTagValues(any(AciParameters.class), any())).thenReturn(response);

        final Map<TagName, ValueDetails> valueDetails = parametricValuesService.getValueDetails(parametricRequest);
        assertThat(valueDetails.size(), is(3));
        assertThat(valueDetails, hasEntry(equalTo(tagNameFactory.buildTagName(elevation)), equalTo(new ValueDetails(-50, 1242, 500.5, 12314, 3))));
        assertThat(valueDetails, hasEntry(equalTo(tagNameFactory.buildTagName(age)), equalTo(new ValueDetails(0, 96, 26, 1314, 100))));
        assertThat(valueDetails, hasEntry(equalTo(tagNameFactory.buildTagName(notThere)), equalTo(new ValueDetails(0d, 0d, 0d, 0d, 0))));
    }

    @Test
    public void getNumericParametricValues() {
        mockBucketResponses(7,
                mockTagValue("1,2", 0),
                mockTagValue("2,3", 5),
                mockTagValue("3,4", 2),
                mockTagValue("4,5", 0),
                mockTagValue("5,6", 0)
        );

        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericDateField"));
        final List<RangeInfo> results = parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, ImmutableMap.of(tagNameFactory.buildTagName("ParametricNumericDateField"), new BucketingParams(5, 1.0, 6.0)));
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

    @Test(expected = IllegalArgumentException.class)
    public void getNumericParametricValuesZeroBucketsZeroBuckets() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.singletonList("ParametricNumericDateField"));
        parametricValuesService.getNumericParametricValuesInBuckets(idolParametricRequest, ImmutableMap.of(tagNameFactory.buildTagName("ParametricNumericDateField"), new BucketingParams(0, 1.0, 5.0)));
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
        when(queryExecutor.executeGetQueryTagValues(any(AciParameters.class), any())).thenReturn(responseData);
        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getDependentValuesFieldNamesFirst() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        final ImmutableMap<FieldTypeParam, List<TagName>> fieldsResponse = ImmutableMap.of(FieldTypeParam.Parametric, Collections.singletonList(tagNameFactory.buildTagName("CATEGORY")));
        when(fieldsService.getFields(any(), eq(FieldTypeParam.Parametric))).thenReturn(fieldsResponse);

        final GetQueryTagValuesResponseData responseData = mockRecursiveResponse();
        when(queryExecutor.executeGetQueryTagValues(any(AciParameters.class), any())).thenReturn(responseData);

        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void dependentParametricValuesNotConfigured() {
        final IdolParametricRequest idolParametricRequest = mockRequest(Collections.emptyList());

        final Map<FieldTypeParam, List<TagName>> response = ImmutableMap.of(FieldTypeParam.Parametric, Collections.emptyList());
        when(fieldsService.getFields(any(), any(FieldTypeParam.class))).thenReturn(response);

        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(idolParametricRequest);
        assertThat(results, is(empty()));
    }

    private IdolParametricRequest mockRequest(final Collection<String> fieldNames) {
        final IdolParametricRequest parametricRequest = mock(IdolParametricRequest.class);
        final List<TagName> tagNames = fieldNames.stream().map(tagNameFactory::buildTagName).collect(Collectors.toList());
        when(parametricRequest.getFieldNames()).thenReturn(tagNames);

        final IdolParametricRequestBuilder parametricRequestBuilder = mock(IdolParametricRequestBuilder.class);
        when(parametricRequestBuilder.maxValues(any())).thenReturn(parametricRequestBuilder);
        when(parametricRequestBuilder.ranges(any())).thenReturn(parametricRequestBuilder);
        when(parametricRequestBuilder.start(any())).thenReturn(parametricRequestBuilder);
        when(parametricRequest.toBuilder()).thenReturn(parametricRequestBuilder);
        when(parametricRequestBuilder.build()).thenReturn(parametricRequest);

        return parametricRequest;
    }

    private GetQueryTagValuesResponseData mockQueryResponse() {
        final GetQueryTagValuesResponseData responseData = new GetQueryTagValuesResponseData();
        final FlatField field = new FlatField();
        field.setTotalValues(5);
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

        when(queryExecutor.executeGetQueryTagValues(any(AciParameters.class), any())).thenReturn(responseData);
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
