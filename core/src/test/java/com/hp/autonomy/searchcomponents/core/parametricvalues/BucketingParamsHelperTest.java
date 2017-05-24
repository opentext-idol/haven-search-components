/*
 * Copyright 2016-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.NumericRangeInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
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
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams<>(5, 0D, 1D), field2, new BucketingParams<>(10, -8.9, 100D)));
    }

    @Test
    public void validateValidDateBucketingParams() {
        final ZonedDateTime now = ZonedDateTime.now();
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams<>(5, now.minusMinutes(5), now), field2, new BucketingParams<>(5, now.minusMinutes(5), now)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateBucketingParamsMissingField() {
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams<>(5, 0D, 1D)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateBucketingParamsInvalidTargetNumberOfBuckets() {
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams<>(0, 0D, 1D), field2, new BucketingParams<>(10, -8.9, 100D)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateBucketingParamsInvalidMinAndMax() {
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams<>(5, 1D, 0D), field2, new BucketingParams<>(10, -8.9, 100D)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateBucketingParamsInvalidMinAndMaxDate() {
        final ZonedDateTime now = ZonedDateTime.now();
        bucketingParamsHelper.validateBucketingParams(parametricRequest, ImmutableMap.of(field1, new BucketingParams<>(5, now, now.minusMinutes(5)), field2, new BucketingParams<>(5, now.minusMinutes(5), now)));
    }

    @Test
    public void calculateSimpleNumericBoundaries() {
        final List<Double> expectedBoundaries = Arrays.asList(0d, 1d, 2d, 3d, 4d);
        assertEquals(expectedBoundaries, bucketingParamsHelper.calculateNumericBoundaries(new BucketingParams<>(4, 0D, 4D)));
    }

    @Test
    public void calculateInexactNumericBoundaries() {
        final int targetNumberOfBuckets = 7;
        final double min = 0D;
        final double max = 1D;
        final List<Double> boundaries = bucketingParamsHelper.calculateNumericBoundaries(new BucketingParams<>(targetNumberOfBuckets, min, max));
        assertThat(boundaries, hasSize(targetNumberOfBuckets + 1));
        assertThat(boundaries.get(0), is(min));
        assertThat(boundaries.get(targetNumberOfBuckets), is(max));
    }

    @Test
    public void calculateSimpleDateBoundaries() {
        final ZonedDateTime max = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        final ZonedDateTime min = max.minusMinutes(3);
        final List<ZonedDateTime> expectedBoundaries = Arrays.asList(min, max.minusMinutes(2), max.minusMinutes(1), max);
        assertEquals(expectedBoundaries, bucketingParamsHelper.calculateDateBoundaries(new BucketingParams<>(3, min, max)));
    }

    @Test
    public void calculateSimpleDateBoundariesWithRounding() {
        final ZonedDateTime max = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).plusNanos(1);
        final ZonedDateTime min = max.minusSeconds(2);
        final ZonedDateTime adjustedMax = max.plusSeconds(1).truncatedTo(ChronoUnit.SECONDS);
        final ZonedDateTime adjustedMin = min.truncatedTo(ChronoUnit.SECONDS);
        final List<ZonedDateTime> expectedBoundaries = Arrays.asList(adjustedMin, adjustedMax.minusSeconds(2), adjustedMax.minusSeconds(1), adjustedMax);
        assertEquals(expectedBoundaries, bucketingParamsHelper.calculateDateBoundaries(new BucketingParams<>(3, min, max)));
    }

    @Test
    public void calculateDateBoundariesRequiringFewerBucketsThanRequested() {
        final ZonedDateTime max = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        final ZonedDateTime min = max.minusSeconds(1);
        final List<ZonedDateTime> expectedBoundaries = Arrays.asList(min, max);
        assertEquals(expectedBoundaries, bucketingParamsHelper.calculateDateBoundaries(new BucketingParams<>(3, min, max)));
    }

    @Test
    public void calculateInexactDateBoundariesRequiringMaxPadding() {
        final ZonedDateTime max = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        final ZonedDateTime min = max.minusSeconds(5);
        final List<ZonedDateTime> expectedBoundaries = Arrays.asList(min, min.plusSeconds(2), min.plusSeconds(4), max.plusSeconds(1));
        assertEquals(expectedBoundaries, bucketingParamsHelper.calculateDateBoundaries(new BucketingParams<>(3, min, max)));
    }

    @Test
    public void calculateInexactDateBoundariesRequiringMinMaxPadding() {
        final ZonedDateTime max = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        final ZonedDateTime min = max.minusSeconds(7);
        final List<ZonedDateTime> expectedBoundaries = Arrays.asList(min.minusSeconds(1), min.plusSeconds(2), max.minusSeconds(2), max.plusSeconds(1));
        assertEquals(expectedBoundaries, bucketingParamsHelper.calculateDateBoundaries(new BucketingParams<>(3, min, max)));
    }

    @Test
    public void calculateInexactDateBoundaries() {
        final int targetNumberOfBuckets = 11;
        final ZonedDateTime max = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        final ZonedDateTime min = max.minusMinutes(1);
        final List<ZonedDateTime> boundaries = bucketingParamsHelper.calculateDateBoundaries(new BucketingParams<>(targetNumberOfBuckets, min, max));
        assertThat(boundaries, hasSize(targetNumberOfBuckets + 1));
        assertThat(boundaries.get(0), lessThanOrEqualTo(min));
        assertThat(boundaries.get(targetNumberOfBuckets), greaterThanOrEqualTo(max));
    }

    @Test
    public void emptyBuckets() {
        @SuppressWarnings("RedundantTypeArguments") // presumably Java bug
        final List<NumericRangeInfo.Value> buckets = bucketingParamsHelper.<Double, Double, NumericRangeInfo.Value>emptyBuckets(Arrays.asList(0.0, 0.5, 1.0), NumericRangeInfo.Value::new);

        assertThat(buckets, is(Arrays.asList(
            new NumericRangeInfo.Value(0.0, 0.5, 0),
            new NumericRangeInfo.Value(0.5, 1.0, 0)
        )));
    }
}
