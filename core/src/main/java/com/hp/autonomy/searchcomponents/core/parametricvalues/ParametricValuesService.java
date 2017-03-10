/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service for retrieving parametric values
 *
 * @param <R> The request type to use
 * @param <Q> The type of the query restrictions object
 * @param <E> The checked exception thrown in the event of an error
 */
public interface ParametricValuesService<R extends ParametricRequest<Q>, Q extends QueryRestrictions<?>, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String PARAMETRIC_VALUES_SERVICE_BEAN_NAME = "parametricValuesService";

    /**
     * The bean name of the default request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String PARAMETRIC_REQUEST_BUILDER_BEAN_NAME = "parametricRequestBuilder";

    /**
     * Special Idol parametric values field which matches the configured default date field
     */
    String AUTN_DATE_FIELD = "AUTN_DATE";

    /**
     * Returns parametric values which match the request restrictions for the requested parametric fields specified.
     *
     * @param parametricRequest Query restrictions and field names
     * @return Parametric values and counts
     * @throws E The error thrown in the event of the platform returning an error response
     */
    Set<QueryTagInfo> getParametricValues(R parametricRequest) throws E;

    /**
     * Get Numeric or NumericDate parametric values as ranges and counts. For each field in the parametricRequest, a
     * BucketingParams must be specified in the bucketingParamsPerField map.
     *
     * @param parametricRequest       Query restrictions and field names
     * @param bucketingParamsPerField Map of fully qualified field name to min, max and number of buckets
     * @return A list of ranges and counts for each field in the parametric request
     * @throws E The error thrown in the event of the platform returning an error response
     */

    List<RangeInfo> getNumericParametricValuesInBuckets(R parametricRequest, final Map<FieldPath, BucketingParams> bucketingParamsPerField) throws E;

    /**
     * Returns parametric values in a hierarchy
     *
     * @param parametricRequest Query restrictions and field names
     * @return The parametric values and counts in a hierarchy
     * @throws E The error thrown in the event of the platform returning an error response
     */
    List<DependentParametricField> getDependentParametricValues(R parametricRequest) throws E;

    /**
     * Get the value details for the fields and restrictions in the given parametric request.
     *
     * @param parametricRequest Field names and query restrictions
     * @return A map of field name to value details
     * @throws E The error thrown in the event of the platform returning an error response
     */
    Map<TagName, ValueDetails> getValueDetails(R parametricRequest) throws E;
}
