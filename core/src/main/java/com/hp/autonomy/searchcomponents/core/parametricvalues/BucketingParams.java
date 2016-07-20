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
    private final double min;
    private final double max;

    @JsonCreator
    public BucketingParams(
            @JsonProperty final int targetNumberOfBuckets,
            @JsonProperty final double min,
            @JsonProperty final double max
    ) {
        this.targetNumberOfBuckets = targetNumberOfBuckets;
        this.min = min;
        this.max = max;
    }
}
