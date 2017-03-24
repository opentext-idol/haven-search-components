/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
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
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
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
    private FieldsService<F, E> fieldsService;
    @Autowired
    private ObjectFactory<FB> fieldsRequestBuilderFactory;

    @Autowired
    protected ObjectFactory<ParametricRequestBuilder<R, Q, ?>> parametricRequestBuilderFactory;
    @Autowired
    protected TagNameFactory tagNameFactory;
    @Autowired
    protected ParametricValuesService<R, Q, E> parametricValuesService;
    @Autowired
    protected TestUtils<Q> testUtils;

    protected abstract FieldsRequestBuilder<F, ?> fieldsRequestParams(final FB fieldsRequestBuilder);

    // Find a parametric field with 2 or more values for the parametric request used in these tests
    protected abstract FieldPath determinePaginatableField();

    protected abstract R noResultsParametricRequest();

    @Test
    public void getParametricValues() throws E {
        final Set<QueryTagInfo> results = parametricValuesService.getParametricValues(createParametricRequest());
        assertThat(results, is(not(empty())));

        final QueryTagInfo tagInfo = results.iterator().next();
        assertThat(tagInfo.getTotalValues(), greaterThanOrEqualTo(0));
    }

    @Test
    public void ranges() throws E {
        final Map<FieldPath, ValueDetails> valueDetailsOutput = parametricValuesService.getValueDetails(createNumericParametricRequest());
        final ValueDetails valueDetails = valueDetailsOutput.get(tagNameFactory.getFieldPath(ParametricValuesService.AUTN_DATE_FIELD));
        final List<RangeInfo> ranges = parametricValuesService.getNumericParametricValuesInBuckets(createNumericParametricRequest(), ImmutableMap.of(tagNameFactory.getFieldPath(ParametricValuesService.AUTN_DATE_FIELD), new BucketingParams(35, valueDetails.getMin(), valueDetails.getMax())));
        assertThat(ranges, not(empty()));
    }

    @Test
    public void rangesNoResults() throws E {
        final List<RangeInfo> ranges = parametricValuesService.getNumericParametricValuesInBuckets(
                noResultsParametricRequest(),
                ImmutableMap.of(tagNameFactory.getFieldPath(ParametricValuesService.AUTN_DATE_FIELD), new BucketingParams(5, 0, 1))
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
    public void getValueDetails() throws E {
        final Map<FieldPath, ValueDetails> valueDetailsMap = parametricValuesService.getValueDetails(createNumericParametricRequest());
        assertThat(valueDetailsMap.size(), is(not(0)));

        for (final Map.Entry<FieldPath, ValueDetails> entry : valueDetailsMap.entrySet()) {
            assertThat(entry.getKey(), not(nullValue()));

            final ValueDetails valueDetails = entry.getValue();

            // min <= average <= max
            assertThat(valueDetails.getMin(), lessThanOrEqualTo(valueDetails.getAverage()));
            assertThat(valueDetails.getAverage(), lessThanOrEqualTo(valueDetails.getMax()));
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

    private R createNumericParametricRequest() {
        return parametricRequestBuilderFactory.getObject()
                .fieldName(tagNameFactory.getFieldPath(ParametricValuesService.AUTN_DATE_FIELD))
                .queryRestrictions(testUtils.buildQueryRestrictions())
                .sort(SortParam.ReverseDate)
                .build();
    }

    protected R createParametricRequest() throws E {
        final FieldsRequestBuilder<F, ?> fieldsRequestBuilder = fieldsRequestParams(fieldsRequestBuilderFactory.getObject());
        fieldsRequestBuilder.fieldType(FieldTypeParam.Parametric);
        final Set<FieldPath> fields = fieldsService.getFields(fieldsRequestBuilder.build()).get(FieldTypeParam.Parametric)
                .stream()
                .map(TagName::getId)
                .collect(Collectors.toSet());

        return parametricRequestBuilderFactory.getObject()
                .fieldNames(fields)
                .queryRestrictions(testUtils.buildQueryRestrictions())
                .build();
    }
}
