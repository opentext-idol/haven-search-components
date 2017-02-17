/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;

import java.util.List;

/**
 * Options for interacting with {@link ParametricValuesService}
 *
 * @param <Q> The type of the query restrictions object
 */
public interface ParametricRequest<Q extends QueryRestrictions<?>> extends RequestObject<ParametricRequest<Q>, ParametricRequestBuilder<?, Q, ?>> {
    int START_DEFAULT = 1;

    /**
     * Fields for which to retrieve parametric values
     *
     * @return Idol field names
     */
    List<TagName> getFieldNames();

    /**
     * @return Index of first value to return for each field (1-based)
     */
    Integer getStart();

    /**
     * Max results to return
     *
     * @return max results to return
     */
    Integer getMaxValues();

    /**
     * Sort criterion
     *
     * @return the criterion by which to sort
     */
    SortParam getSort();

    /**
     * Range information for numeric bucketing
     *
     * @return Range information for numeric bucketing per field
     */
    List<Range> getRanges();

    /**
     * Restrictions on the underlying query
     *
     * @return Restrictions on the underlying query
     */
    Q getQueryRestrictions();

    /**
     * Whether to apply QMS rules
     *
     * @return whether to apply QMS rules
     */
    boolean isModified();
}
