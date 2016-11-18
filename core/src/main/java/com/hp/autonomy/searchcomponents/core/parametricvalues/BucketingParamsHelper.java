/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Class providing helpful methods for interacting with {@link BucketingParams}
 */
public interface BucketingParamsHelper {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String BUCKETING_PARAMS_HELPER_BEAN_NAME = "bucketingParamsHelper";

    /**
     * Verify that the fields specified in the parametric request are matched by a valid entry in the bucketing params.
     */
    <R extends ParametricRequest<S>, S extends Serializable> void validateBucketingParams(R parametricRequest,
                                                                                          Map<String, BucketingParams> bucketingParamsPerField);

    /**
     * Calculate the boundary values (including both the min and the max) of the buckets specified in the BucketingParams.
     *
     * @param bucketingParams The min, max and target number of buckets
     * @return List of boundary values, including the min and the max value
     */
    List<Double> calculateBoundaries(BucketingParams bucketingParams);
}
