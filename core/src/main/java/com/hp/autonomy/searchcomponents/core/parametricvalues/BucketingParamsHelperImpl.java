/*
 * Copyright 2016-2017 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfoValue;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
    public <T extends Comparable<? super T> & Serializable> void validateBucketingParams(
        final ParametricRequest<?> parametricRequest,
        final Map<FieldPath, BucketingParams<T>> bucketingParamsPerField
    ) {
        for(final FieldPath fieldPath : parametricRequest.getFieldNames()) {
            final BucketingParams<T> bucketingParams = bucketingParamsPerField.get(fieldPath);

            if(bucketingParams == null) {
                throw new IllegalArgumentException("Missing bucketing params for " + fieldPath);
            }

            if(bucketingParams.getTargetNumberOfBuckets() <= 0) {
                throw new IllegalArgumentException("Invalid target number of buckets for " + fieldPath);
            }

            if(bucketingParams.getMin().compareTo(bucketingParams.getMax()) > 0) {
                throw new IllegalArgumentException("Invalid range for " + fieldPath);
            }
        }
    }

    @Override
    public List<Double> calculateNumericBoundaries(final BucketingParams<Double> bucketingParams) {
        final List<Double> boundaries = new LinkedList<>();
        final double bucketSize = (bucketingParams.getMax() - bucketingParams.getMin()) / bucketingParams.getTargetNumberOfBuckets();
        // Generate boundaries, including both the bucket min and max (hence <=)
        for(int i = 0; i <= bucketingParams.getTargetNumberOfBuckets(); i++) {
            final double boundary = bucketingParams.getMin() + bucketSize * i;
            boundaries.add(boundary);
        }

        return boundaries;
    }

    @Override
    public List<ZonedDateTime> calculateDateBoundaries(final BucketingParams<ZonedDateTime> bucketingParams) {
        final ZonedDateTime min = bucketingParams.getMin().truncatedTo(ChronoUnit.SECONDS);
        final ZonedDateTime max = bucketingParams.getMax().plusSeconds(1).minusNanos(1).truncatedTo(ChronoUnit.SECONDS);

        int targetNumberOfBuckets = bucketingParams.getTargetNumberOfBuckets();
        double bucketSize = (double)(max.toEpochSecond() - min.toEpochSecond()) / targetNumberOfBuckets;
        // IDOL's stored date resolution is 1s inside the epoch range, 1m outside
        final double minBucketSize =
            (min.toEpochSecond() < 0 || max.toEpochSecond() > 2_147_483_647) ? 60 : 1;
        if(bucketSize < minBucketSize) {
            bucketSize = minBucketSize;
            targetNumberOfBuckets =
                (int) Math.ceil((max.toEpochSecond() - min.toEpochSecond()) / bucketSize);
        }

        final Duration bucketDuration = Duration.ofSeconds((long)Math.ceil(bucketSize));
        final Duration totalBucketedDuration = bucketDuration.multipliedBy(targetNumberOfBuckets);
        final Duration totalDesiredDuration = Duration.between(min, max);
        final Duration padding = totalBucketedDuration.minus(totalDesiredDuration).dividedBy(2);

        final List<ZonedDateTime> boundaries = new LinkedList<>();
        // If bucket size rounding causes total bucketing range to exceed specified range, spread difference between min
        // and max (adding excess to max where exact distribution not possible)
        ZonedDateTime boundary = min.minus(padding).plusSeconds(1).minusNanos(1).truncatedTo(ChronoUnit.SECONDS);
        do {
            boundaries.add(boundary);
            boundary = boundary.plus(bucketDuration);
        } while(boundaries.size() <= targetNumberOfBuckets);

        if(boundaries.size() == 1) {
            // IDOL uses ranges which are lower bound inclusive and upper bound exclusive; so if there's one data point
            //   it throws an 'Invalid parametric range specification.' error.
            // Workaround by creating a 1-second range, since 1 second is the minimum time that IDOL will resolve.
            boundaries.add(boundaries.get(0).plusSeconds(1));
        }

        return boundaries;
    }

    @Override
    public <T extends Comparable<? super T> & Serializable, D extends Comparable<D> & Serializable, V extends RangeInfoValue<T, D>> List<V>
    emptyBuckets(final List<T> boundaries, final RangeInfoValue.Constructor<T, D, V> constructor) {
        final List<V> values = new LinkedList<>();

        for(int i = 0; i < boundaries.size() - 1; i++) {
            values.add(constructor.apply(boundaries.get(i), boundaries.get(i + 1), 0));
        }

        return values;
    }
}
