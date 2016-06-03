/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import lombok.Getter;

abstract class AbstractBucketSizeEvaluator implements BucketSizeEvaluator {
    @Getter
    private final double bucketSize;
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private final BucketingParams bucketingParams;

    AbstractBucketSizeEvaluator(final BucketingParams bucketingParams) {
        this.bucketingParams = bucketingParams;
        bucketSize = evaluateBucketSize();
    }

    protected abstract double evaluateBucketSize();

    @Override
    public double getMin() {
        return adjustBucketMin(bucketingParams.getMin());
    }

    @Override
    public double getMax() {
        return adjustBucketMax(bucketingParams.getMax());
    }

    @Override
    public int getTargetNumberOfBuckets() {
        return bucketingParams.getTargetNumberOfBuckets();
    }
}
