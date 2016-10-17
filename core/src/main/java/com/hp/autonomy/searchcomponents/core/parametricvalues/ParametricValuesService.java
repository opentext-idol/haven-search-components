/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.types.idol.responses.RecursiveField;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ParametricValuesService<R extends ParametricRequest<S>, S extends Serializable, E extends Exception> {

    String AUTN_DATE_FIELD = "autn_date";

    Set<QueryTagInfo> getAllParametricValues(R parametricRequest) throws E;

    /**
     * Get Numeric or NumericDate parametric values as ranges and counts. For each field in the parametricRequest, a
     * BucketingParams must be specified in the bucketingParamsPerField map.
     * @param parametricRequest Query restrictions and field names
     * @param bucketingParamsPerField Map of fully qualified field name to min, max and number of buckets
     * @return A list of ranges and counts for each field in the parametric request
     * @throws E
     */
    List<RangeInfo> getNumericParametricValuesInBuckets(R parametricRequest, final Map<String, BucketingParams> bucketingParamsPerField) throws E;

    List<RecursiveField> getDependentParametricValues(R parametricRequest) throws E;

    /**
     * Get the value details for the fields and restrictions in the given parametric request.
     * @param parametricRequest Field names and query restrictions
     * @return A map of field name to value details
     * @throws E
     */
    Map<TagName, ValueDetails> getValueDetails(R parametricRequest) throws E;

}
