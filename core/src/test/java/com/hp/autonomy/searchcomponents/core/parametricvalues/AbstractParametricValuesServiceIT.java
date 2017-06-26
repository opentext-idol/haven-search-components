/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.requests.idol.actions.tags.DateRangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.DateValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.NumericRangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.NumericValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringJavaAutowiringInspection"})
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
public abstract class AbstractParametricValuesServiceIT<
    R extends ParametricRequest<Q>,
    F extends FieldsRequest,
    FB extends FieldsRequestBuilder<F, ?>,
    Q extends QueryRestrictions<S>,
    S extends Serializable,
    E extends Exception
    > {
    @Autowired
    protected ObjectFactory<ParametricRequestBuilder<R, Q, ?>> parametricRequestBuilderFactory;
    @Autowired
    protected TagNameFactory tagNameFactory;
    @Autowired
    protected ParametricValuesService<R, Q, E> parametricValuesService;
    @Autowired
    protected TestUtils<Q> testUtils;
    @Autowired
    private FieldsService<F, E> fieldsService;
    @Autowired
    private ObjectFactory<FB> fieldsRequestBuilderFactory;

    protected abstract FieldsRequestBuilder<F, ?> fieldsRequestParams(final FB fieldsRequestBuilder);

    // Find a parametric field with 2 or more values for the parametric request used in these tests
    protected abstract FieldPath determinePaginatableField();

    protected abstract R noResultsParametricRequest(final Collection<FieldPath> fields);

    @Test
    public void getParametricValues() throws E {
        final Set<QueryTagInfo> results = parametricValuesService.getParametricValues(createParametricRequest());
        assertThat(results, is(not(empty())));

        final QueryTagInfo tagInfo = results.iterator().next();
        assertThat(tagInfo.getTotalValues(), greaterThanOrEqualTo(0));
    }

    @Test
    public void numericRanges() throws E {
        final R numericParametricRequest = createNumericParametricRequest();
        final Map<FieldPath, NumericValueDetails> valueDetailsOutput = parametricValuesService.getNumericValueDetails(numericParametricRequest);
        final Map<FieldPath, BucketingParams<Double>> bucketingParamsPerField = valueDetailsOutput.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> new BucketingParams<>(35, e.getValue().getMin(), e.getValue().getMax())));
        final List<NumericRangeInfo> ranges = parametricValuesService.getNumericParametricValuesInBuckets(numericParametricRequest, bucketingParamsPerField);
        assertThat(ranges, not(empty()));
    }

    @Test
    public void numericRangesNoResults() throws E {
        final Set<FieldPath> fields = getFieldsOfType(FieldTypeParam.Numeric);
        final List<NumericRangeInfo> ranges = parametricValuesService.getNumericParametricValuesInBuckets(
            noResultsParametricRequest(fields),
            fields.stream().collect(Collectors.toMap(Function.identity(), x -> new BucketingParams<>(5, 0D, 1D)))
        );

        assertThat(ranges, hasSize(fields.size()));
        assertThat(ranges.get(0).getValues(), hasSize(5));
        ranges.get(0).getValues().forEach(value -> assertThat(value.getCount(), is(0)));
    }

    @Test
    public void dateRanges() throws E {
        final R dateParametricRequest = createDateParametricRequest();
        final Map<FieldPath, DateValueDetails> valueDetailsOutput = parametricValuesService.getDateValueDetails(dateParametricRequest);
        final Map<FieldPath, BucketingParams<ZonedDateTime>> bucketingParamsPerField = valueDetailsOutput.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> new BucketingParams<>(35, e.getValue().getMin(), e.getValue().getMax())));
        final List<DateRangeInfo> ranges = parametricValuesService.getDateParametricValuesInBuckets(dateParametricRequest, bucketingParamsPerField);
        assertThat(ranges, not(empty()));
    }

    @Test
    public void dateRangesOutside1970and2038() throws E {
        final ZonedDateTime min = ZonedDateTime.parse("-0001-01-01T01:01:00Z");
        final ZonedDateTime max = ZonedDateTime.parse("3000-03-03T03:03:00Z");

        final R dateParametricRequest = createDateParametricRequest();
        final Map<FieldPath, DateValueDetails> valueDetailsOutput = parametricValuesService.getDateValueDetails(dateParametricRequest);
        final Map<FieldPath, BucketingParams<ZonedDateTime>> bucketingParamsPerField = valueDetailsOutput.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> new BucketingParams<>(35, min, max)));
        final List<DateRangeInfo> ranges = parametricValuesService.getDateParametricValuesInBuckets(dateParametricRequest, bucketingParamsPerField);
        assertThat(ranges, not(empty()));
        final DateRangeInfo dateRangeInfo = ranges.get(0);
        assertThat(dateRangeInfo.getMin(), lessThanOrEqualTo(min));
        assertThat(dateRangeInfo.getMax(), greaterThanOrEqualTo(max));
        assertThat(dateRangeInfo.getValues(), hasSize(35));
        assertThat(dateRangeInfo.getValues().get(0).getMin(), equalTo(dateRangeInfo.getMin().withSecond(0)));
        assertThat(dateRangeInfo.getValues().get(34).getMax(), equalTo(dateRangeInfo.getMax().withSecond(0)));
    }

    @Test
    public void dateRangesNoResults() throws E {
        final FieldPath fieldPath = tagNameFactory.getFieldPath(ParametricValuesService.AUTN_DATE_FIELD);
        final List<DateRangeInfo> ranges = parametricValuesService.getDateParametricValuesInBuckets(
            noResultsParametricRequest(Collections.singleton(fieldPath)),
            ImmutableMap.of(fieldPath, new BucketingParams<>(5, ZonedDateTime.now().minusMinutes(5), ZonedDateTime.now()))
        );

        assertThat(ranges, hasSize(1));
        assertThat(ranges.get(0).getValues(), hasSize(5));
        ranges.get(0).getValues().forEach(value -> assertThat(value.getCount(), is(0)));
    }

    @Test
    public void getDependentParametricValues() throws E {
        final R request = createParametricRequest();
        final Collection<DependentParametricField> results = parametricValuesService.getDependentParametricValues(request);
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getNumericValueDetails() throws E {
        final Map<FieldPath, NumericValueDetails> valueDetailsMap = parametricValuesService.getNumericValueDetails(createNumericParametricRequest());
        validateValueDetails(valueDetailsMap);
    }

    @Test
    public void getDateValueDetails() throws E {
        final Map<FieldPath, DateValueDetails> valueDetailsMap = parametricValuesService.getDateValueDetails(createDateParametricRequest());
        validateValueDetails(valueDetailsMap);
    }

    private <T extends Comparable<T>, U extends T, V extends ValueDetails<U>> void validateValueDetails(final Map<FieldPath, V> valueDetailsMap) {
        assertThat(valueDetailsMap.size(), is(not(0)));

        for(final Map.Entry<FieldPath, V> entry : valueDetailsMap.entrySet()) {
            assertThat(entry.getKey(), not(nullValue()));

            final V valueDetails = entry.getValue();

            // min <= average <= max
            final T max = valueDetails.getMax();
            final T min = valueDetails.getMin();
            final T average = valueDetails.getAverage();
            assertThat(min, lessThanOrEqualTo(average));
            assertThat(average, lessThanOrEqualTo(max));
        }
    }

    @Test
    public void getPaginatedParametricValues() throws E {
        final FieldPath fieldName = determinePaginatableField();

        final R page1Request = parametricRequestBuilderFactory.getObject()
            .queryRestrictions(testUtils.buildQueryRestrictions())
            .fieldName(fieldName)
            .start(1)
            .maxValues(1)
            .build();

        final Set<QueryTagInfo> page1 = parametricValuesService.getParametricValues(page1Request);

        final R page2Request = parametricRequestBuilderFactory.getObject()
            .queryRestrictions(testUtils.buildQueryRestrictions())
            .fieldName(fieldName)
            .start(2)
            .maxValues(2)
            .build();

        final Set<QueryTagInfo> page2 = parametricValuesService.getParametricValues(page2Request);

        assertThat(page1, hasSize(1));
        assertThat(page2, hasSize(1));
        assertThat(page1, not(equalTo(page2)));
    }

    private R createNumericParametricRequest() throws E {
        final Set<FieldPath> fields = getFieldsOfType(FieldTypeParam.Numeric);

        return parametricRequestBuilderFactory.getObject()
            .fieldNames(fields)
            .queryRestrictions(testUtils.buildQueryRestrictions())
            .sort(SortParam.ReverseDate)
            .build();
    }

    private R createDateParametricRequest() {
        return parametricRequestBuilderFactory.getObject()
            .fieldName(tagNameFactory.getFieldPath(ParametricValuesService.AUTN_DATE_FIELD))
            .queryRestrictions(testUtils.buildQueryRestrictions())
            .sort(SortParam.ReverseDate)
            .build();
    }

    protected R createParametricRequest() throws E {
        final Set<FieldPath> fields = getFieldsOfType(FieldTypeParam.Parametric);

        return parametricRequestBuilderFactory.getObject()
            .fieldNames(fields)
            .queryRestrictions(testUtils.buildQueryRestrictions())
            .build();
    }

    private Set<FieldPath> getFieldsOfType(final FieldTypeParam typeParam) throws E {
        final FieldsRequestBuilder<F, ?> fieldsRequestBuilder = fieldsRequestParams(fieldsRequestBuilderFactory.getObject());
        fieldsRequestBuilder.fieldType(typeParam);
        return fieldsService.getFields(fieldsRequestBuilder.build()).get(typeParam)
            .stream()
            .map(TagName::getId)
            .collect(Collectors.toSet());
    }
}
