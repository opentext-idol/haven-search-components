/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import org.springframework.stereotype.Component;

@Component
public class AdaptiveBucketSizeEvaluatorFactoryImpl implements AdaptiveBucketSizeEvaluatorFactory {
    @Override
    public BucketSizeEvaluator getBucketSizeEvaluator(final BucketingParams bucketingParams) {
        BucketSizeEvaluator bucketSizeEvaluator = new IntegerBucketSizeEvaluator(bucketingParams);
        final double numberOfBuckets = (bucketSizeEvaluator.getMax() - bucketSizeEvaluator.getMin()) / bucketSizeEvaluator.getBucketSize();
        final int minimumAcceptableNumberOfBuckets = bucketSizeEvaluator.getTargetNumberOfBuckets() / 2;

        if (numberOfBuckets < minimumAcceptableNumberOfBuckets) {
            final BucketSizeEvaluator continuousBucketSizeEvaluator = new ContinuousBucketSizeEvaluator(bucketingParams);
            final double revisedNumberOfBuckets = (continuousBucketSizeEvaluator.getMax() - continuousBucketSizeEvaluator.getMin()) / continuousBucketSizeEvaluator.getBucketSize();

            if (revisedNumberOfBuckets > numberOfBuckets) {
                bucketSizeEvaluator = continuousBucketSizeEvaluator;
            }
        }

        return bucketSizeEvaluator;
    }
}
