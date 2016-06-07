/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

class ContinuousBucketSizeEvaluator extends AbstractBucketSizeEvaluator {
    ContinuousBucketSizeEvaluator(final BucketingParams bucketingParams) {
        super(bucketingParams);
    }

    @Override
    protected double evaluateBucketSize() {
        return (getMax() - getMin()) / getTargetNumberOfBuckets();
    }

    @Override
    public double adjustBucketMin(final double min) {
        return min;
    }

    @Override
    public double adjustBucketMax(final double max) {
        return max;
    }
}
