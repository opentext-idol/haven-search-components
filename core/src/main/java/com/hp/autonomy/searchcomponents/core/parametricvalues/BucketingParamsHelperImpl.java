package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParamsHelper.BUCKETING_PARAMS_HELPER_BEAN_NAME;

/**
 * Default implementation of the {@link BucketingParamsHelper}
 */
@Component(BUCKETING_PARAMS_HELPER_BEAN_NAME)
class BucketingParamsHelperImpl implements BucketingParamsHelper {
    @Override
    public <R extends ParametricRequest<Q>, Q extends QueryRestrictions<?>> void validateBucketingParams(final R parametricRequest,
                                                                                                         final Map<TagName, BucketingParams> bucketingParamsPerField) {
        for (final TagName tagName : parametricRequest.getFieldNames()) {
            final BucketingParams bucketingParams = bucketingParamsPerField.get(tagName);

            if (bucketingParams == null) {
                throw new IllegalArgumentException("Missing bucketing params for " + tagName);
            }

            if (bucketingParams.getTargetNumberOfBuckets() <= 0) {
                throw new IllegalArgumentException("Invalid target number of buckets for " + tagName);
            }

            if (bucketingParams.getMin() > bucketingParams.getMax()) {
                throw new IllegalArgumentException("Invalid range for " + tagName);
            }
        }
    }

    @Override
    public List<Double> calculateBoundaries(final BucketingParams bucketingParams) {
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
