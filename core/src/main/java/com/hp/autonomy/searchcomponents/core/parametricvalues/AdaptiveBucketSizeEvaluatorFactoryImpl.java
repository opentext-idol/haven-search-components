/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import org.springframework.stereotype.Component;

@Component
public class AdaptiveBucketSizeEvaluatorFactoryImpl implements AdaptiveBucketSizeEvaluatorFactory {
    @Override
    public BucketSizeEvaluator getBucketSizeEvaluator(final double maxValue, final double minValue, final int targetNumberOfBuckets) {
        BucketSizeEvaluator bucketSizeEvaluator = new IntegerBucketSizeEvaluator(maxValue, minValue, targetNumberOfBuckets);
        final double numberOfBuckets = (bucketSizeEvaluator.adjustMax(maxValue) - bucketSizeEvaluator.adjustMin(minValue)) / bucketSizeEvaluator.getBucketSize();
        if (numberOfBuckets < targetNumberOfBuckets / 2) {
            bucketSizeEvaluator = new ContinuousBucketSizeEvaluator(maxValue, minValue, targetNumberOfBuckets);
        }

        return bucketSizeEvaluator;
    }
}
