/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BucketSizeEvaluatorTest {
    private AdaptiveBucketSizeEvaluatorFactory factory;

    @Before
    public void setUp() {
        factory = new AdaptiveBucketSizeEvaluatorFactoryImpl();
    }

    @Test
    public void discrete() {
        final BucketSizeEvaluator bucketSizeEvaluator = factory.getBucketSizeEvaluator(new BucketingParams(35, 0.0, 100.0));
        assertEquals(3, bucketSizeEvaluator.getBucketSize(), 0);
        assertEquals(0, bucketSizeEvaluator.getMin(), 0);
        assertEquals(101, bucketSizeEvaluator.getMax(), 0);
    }

    @Test
    public void continuous() {
        final BucketSizeEvaluator bucketSizeEvaluator = factory.getBucketSizeEvaluator(new BucketingParams(35, 0.3, 99.7));
        assertEquals(3, bucketSizeEvaluator.getBucketSize(), 0);
        assertEquals(0, bucketSizeEvaluator.getMin(), 0);
        assertEquals(100, (int) bucketSizeEvaluator.getMax(), 0);
    }

    @Test
    public void continuousSmallRange() {
        final BucketSizeEvaluator bucketSizeEvaluator = factory.getBucketSizeEvaluator(new BucketingParams(35, 0.1, 0.9));
        assertEquals(0.02, bucketSizeEvaluator.getBucketSize(), 0.005);
        assertEquals(0.1, bucketSizeEvaluator.getMin(), 0.01);
        assertEquals(0.9, bucketSizeEvaluator.getMax(), 0.01);
    }

    @Test
    public void singleDataPoint() {
        final BucketSizeEvaluator bucketSizeEvaluator = factory.getBucketSizeEvaluator(new BucketingParams(35, 1.0, 1.0));
        assertEquals(1, bucketSizeEvaluator.getBucketSize(), 0);
        assertEquals(1, bucketSizeEvaluator.getMin(), 0);
        assertEquals(2, bucketSizeEvaluator.getMax(), 0);
    }
}
