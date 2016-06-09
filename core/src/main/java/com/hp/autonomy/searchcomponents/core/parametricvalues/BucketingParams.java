/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class BucketingParams {
    private final int targetNumberOfBuckets;
    private Double min;
    private Double max;
    private boolean unlimitedMax;

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

    public BucketingParams(final BucketingParams bucketingParams, final double absoluteMin, final double absoluteMax) {
        targetNumberOfBuckets = bucketingParams.targetNumberOfBuckets;
        min = bucketingParams.min != null ? bucketingParams.min : absoluteMin;

        if (bucketingParams.max != null) {
            max = bucketingParams.max;
        } else {
            max = absoluteMax;
            unlimitedMax = true;
        }
    }
}
