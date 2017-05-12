/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfoValue;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.time.ZonedDateTime;
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
     *
     * @param parametricRequest       Query restrictions and field names
     * @param bucketingParamsPerField Map of fully qualified field name to min, max and number of buckets
     * @param <T>                     The type of the data
     */
    <T extends Comparable<? super T> & Serializable> void validateBucketingParams(final ParametricRequest<?> parametricRequest,
                                                                                  final Map<FieldPath, BucketingParams<T>> bucketingParamsPerField);

    /**
     * Calculate the boundary values (including both the min and the max) of the buckets specified in the BucketingParams.
     *
     * @param bucketingParams The min, max and target number of buckets
     * @return List of boundary values, including the min and the max value
     */
    List<Double> calculateNumericBoundaries(BucketingParams<Double> bucketingParams);

    /**
     * Calculate the boundary values (including both the min and the max) of the buckets specified in the BucketingParams.
     *
     * @param bucketingParams The min, max and target number of buckets
     * @return List of boundary values, including the min and the max value
     */
    List<ZonedDateTime> calculateDateBoundaries(BucketingParams<ZonedDateTime> bucketingParams);

    /**
     * Generate empty buckets for the given boundaries. This is useful because GetQueryTagValues returns no buckets if
     * no documents matched the query restrictions.
     *
     * @param boundaries  Bucket boundaries
     * @param constructor Bucket value constructor
     * @param <T>         bucket data type
     * @return List of empty buckets
     */
    <T extends Comparable<? super T> & Serializable, D extends Comparable<D> & Serializable, V extends RangeInfoValue<T, D>>
    List<V> emptyBuckets(List<T> boundaries, RangeInfoValue.Constructor<T, D, V> constructor);
}
