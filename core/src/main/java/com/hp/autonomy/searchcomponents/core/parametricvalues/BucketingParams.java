/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BucketingParams {
    private final int targetNumberOfBuckets;
    private Double min;
    private Double max;

    public BucketingParams(final int targetNumberOfBuckets) {
        this.targetNumberOfBuckets = targetNumberOfBuckets;
    }

    @JsonCreator
    public BucketingParams(@JsonProperty final int targetNumberOfBuckets,
                           @JsonProperty final Double min,
                           @JsonProperty final Double max) {
        this.targetNumberOfBuckets = targetNumberOfBuckets;
        this.min = min;
        this.max = max;
    }

    public BucketingParams(final BucketingParams bucketingParams, final double min, final double max) {
        targetNumberOfBuckets = bucketingParams.targetNumberOfBuckets;
        this.min = bucketingParams.min != null ? bucketingParams.min : min;
        this.max = bucketingParams.max != null ? bucketingParams.max : max;
    }
}
