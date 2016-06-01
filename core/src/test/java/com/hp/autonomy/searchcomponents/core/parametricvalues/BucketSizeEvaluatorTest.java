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
        final BucketSizeEvaluator bucketSizeEvaluator = factory.getBucketSizeEvaluator(100, 0, 35);
        assertEquals(3, (int) bucketSizeEvaluator.getBucketSize());
        assertEquals(0, (int) bucketSizeEvaluator.adjustMin(0));
        assertEquals(101, (int) bucketSizeEvaluator.adjustMax(100));
    }

    @Test
    public void continuous() {
        final BucketSizeEvaluator bucketSizeEvaluator = factory.getBucketSizeEvaluator(99.7, 0.3, 35);
        assertEquals(3, (int) bucketSizeEvaluator.getBucketSize());
        assertEquals(0, (int) bucketSizeEvaluator.adjustMin(0.3));
        assertEquals(100, (int) bucketSizeEvaluator.adjustMax(99.7));
    }

    @Test
    public void continuousSmallRange() {
        final BucketSizeEvaluator bucketSizeEvaluator = factory.getBucketSizeEvaluator(0.9, 0.1, 35);
        assertEquals(0.02, bucketSizeEvaluator.getBucketSize(), 0.005);
        assertEquals(0.1, bucketSizeEvaluator.adjustMin(0.1), 0.01);
        assertEquals(0.9, bucketSizeEvaluator.adjustMax(0.9), 0.01);
    }
}
