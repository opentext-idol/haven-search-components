/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import lombok.Getter;

class IntegerBucketSizeEvaluator implements BucketSizeEvaluator {
    @Getter
    private final double bucketSize;

    IntegerBucketSizeEvaluator(final double maxValue, final double minValue, final int targetNumberOfBuckets) {
        bucketSize = Math.ceil((adjustMax(maxValue) - adjustMin(minValue)) / targetNumberOfBuckets);
    }

    @Override
    public double adjustMin(final double min) {
        return Math.floor(min);
    }

    @Override
    public double adjustMax(final double max) {
        return Math.floor(max) + 1;
    }
}
