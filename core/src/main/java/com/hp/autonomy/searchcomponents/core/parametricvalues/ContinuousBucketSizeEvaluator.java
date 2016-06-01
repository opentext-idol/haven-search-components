/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import lombok.Getter;

class ContinuousBucketSizeEvaluator implements BucketSizeEvaluator {
    @Getter
    private final double bucketSize;

    ContinuousBucketSizeEvaluator(final double maxValue, final double minValue, final int targetNumberOfBuckets) {
        bucketSize = (adjustMax(maxValue) - adjustMin(minValue)) / targetNumberOfBuckets;
    }

    @Override
    public double adjustMin(final double min) {
        return min;
    }

    @Override
    public double adjustMax(final double max) {
        return max;
    }
}
