package com.hp.autonomy.searchcomponents.core.parametricvalues;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the ParametricValuesService
 */
public abstract class AbstractParametricValuesService<R extends ParametricRequest<S>, S extends Serializable, E extends Exception> implements ParametricValuesService<R, S, E> {
    /**
     * Verify that the fields specified in the parametric request are matched by a valid entry in the bucketing params.
     */
    protected void validateBucketingParams(final R parametricRequest, final Map<String, BucketingParams> bucketingParamsPerField) {
        for (final String fieldName : parametricRequest.getFieldNames()) {
            final BucketingParams bucketingParams = bucketingParamsPerField.get(fieldName);

            if (bucketingParams == null) {
                throw new IllegalArgumentException("Missing bucketing params for " + fieldName);
            }

            if (bucketingParams.getTargetNumberOfBuckets() <= 0) {
                throw new IllegalArgumentException("Invalid target number of buckets for " + fieldName);
            }

            if (bucketingParams.getMin() > bucketingParams.getMax()) {
                throw new IllegalArgumentException("Invalid range for " + fieldName);
            }
        }
    }

    /**
     * Calculate the boundary values (including both the min and the max) of the buckets specified in the BucketingParams.
     * @param bucketingParams The min, max and target number of buckets
     * @return List of boundary values, including the min and the max value
     */
    protected List<Double> calculateBoundaries(final BucketingParams bucketingParams) {
        final double bucketSize = (bucketingParams.getMax() - bucketingParams.getMin()) / bucketingParams.getTargetNumberOfBuckets();
        final List<Double> boundaries = new LinkedList<>();

        // Generate boundaries, including both the bucket min and max (hence <=)
        for (int i = 0; i <= bucketingParams.getTargetNumberOfBuckets(); i++) {
            final double boundary = bucketingParams.getMin() + bucketSize * i;
            boundaries.add(boundary);
        }

        return boundaries;
    }
}
