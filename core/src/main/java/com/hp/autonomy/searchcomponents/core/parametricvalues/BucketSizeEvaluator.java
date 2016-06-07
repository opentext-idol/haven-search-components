/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

public interface BucketSizeEvaluator {
    double getBucketSize();

    double getMin();

    double getMax();

    int getTargetNumberOfBuckets();

    boolean unboundedMax();

    double adjustBucketMin(double min);

    double adjustBucketMax(double max);
}
