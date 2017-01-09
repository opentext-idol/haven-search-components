/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BucketingParamsHelperTest {
    @Mock
    private ParametricRequest<?> parametricRequest;
    @Mock
    private TagName field1;
    @Mock
    private TagName field2;

    private BucketingParamsHelper bucketingParamsHelper;

    @Before
    public void setUp() {
        when(field1.getId()).thenReturn("field1");
        when(field2.getId()).thenReturn("field2");
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
}
