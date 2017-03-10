/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BucketingParamsHelperTest {
    @Mock
    private ParametricRequest<?> parametricRequest;
    @Mock
    private FieldPath field1;
    @Mock
    private FieldPath field2;

    private BucketingParamsHelper bucketingParamsHelper;

    @Before
    public void setUp() {
        when(parametricRequest.getFieldNames()).thenReturn(Arrays.asList(field1, field2));

        bucketingParamsHelper = new BucketingParamsHelperImpl();
    }

    @Test
    public void validateValidBucketingParams() {
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams(5, 0, 1), field2, new BucketingParams(10, -8.9, 100)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateBucketingParamsMissingField() {
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams(5, 0, 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateBucketingParamsInvalidTargetNumberOfBuckets() {
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams(0, 0, 1), field2, new BucketingParams(10, -8.9, 100)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateBucketingParamsInvalidMinAndMax() {
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams(5, 1, 0), field2, new BucketingParams(10, -8.9, 100)));
    }

    @Test
    public void calculateBoundaries() {
        final List<Double> expectedBoundaries = Arrays.asList(0d, 1d, 2d, 3d, 4d);
        assertEquals(expectedBoundaries, bucketingParamsHelper.calculateBoundaries(new BucketingParams(4, 0, 4)));
    }

    @Test
    public void emptyBuckets() {
        final List<RangeInfo.Value> buckets = bucketingParamsHelper.emptyBuckets(Arrays.asList(0.0, 0.5, 1.0));

        assertThat(buckets, is(Arrays.asList(
                new RangeInfo.Value(0.0, 0.5, 0),
                new RangeInfo.Value(0.5, 1.0, 0)
        )));
    }
}
