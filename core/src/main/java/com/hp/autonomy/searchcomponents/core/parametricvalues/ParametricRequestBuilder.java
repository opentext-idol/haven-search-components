/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;

import java.util.Collection;

/**
 * Builder methods common to all request implementations
 *
 * @param <P> The type of the request implementation
 * @param <Q> The type of the query restrictions object
 */
@SuppressWarnings("unused")
public interface ParametricRequestBuilder<P extends ParametricRequest<Q>, Q extends QueryRestrictions<?>, B extends ParametricRequestBuilder<P, Q, B>>
        extends RequestObjectBuilder<ParametricRequest<Q>, ParametricRequestBuilder<?, Q, ?>> {
    /**
     * Sets a field for which to retrieve parametric values
     *
     * @param fieldName Field for which to retrieve parametric values
     * @return the builder (for chaining)
     */
    B fieldName(String fieldName);

    /**
     * Sets fields for which to retrieve parametric values
     *
     * @param fieldNames Fields for which to retrieve parametric values
     * @return the builder (for chaining)
     */
    B fieldNames(Collection<? extends String> fieldNames);

    /**
     * Clears collection of fields for which to retrieve parametric values
     *
     * @return the builder (for chaining)
     */
    B clearFieldNames();

    /**
     * Sets max results to return
     *
     * @param maxValues Max results to return
     * @return the builder (for chaining)
     */
    B maxValues(Integer maxValues);

    /**
     * Sets the criterion by which to sort
     *
     * @param sort The criterion by which to sort
     * @return the builder (for chaining)
     */
    B sort(SortParam sort);

    /**
     * Sets Range information for numeric bucketing for a single field
     *
     * @param range Range information for numeric bucketing for a single field
     * @return the builder (for chaining)
     */
    B range(Range range);

    /**
     * Sets range information for numeric bucketing per field
     *
     * @param ranges Range information for numeric bucketing per field
     * @return the builder (for chaining)
     */
    B ranges(Collection<? extends Range> ranges);

    /**
     * Clears collection of numeric range information
     *
     * @return the builder (for chaining)
     */
    B clearRanges();

    /**
     * Sets the restrictions on the underlying query
     *
     * @param queryRestrictions The restrictions on the underlying query
     * @return the builder (for chaining)
     */
    B queryRestrictions(Q queryRestrictions);

    /**
     * Sets whether to apply QMS rules
     *
     * @param modified Whether to apply QMS rules
     * @return the builder (for chaining)
     */
    B modified(boolean modified);

    /**
     * {@inheritDoc}
     */
    @Override
    P build();
}
