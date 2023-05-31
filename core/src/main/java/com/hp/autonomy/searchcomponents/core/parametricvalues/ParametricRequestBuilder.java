/*
 * Copyright 2015-2017 Open Text.
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

import com.hp.autonomy.aci.content.ranges.ParametricFieldRange;
import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
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
    B fieldName(FieldPath fieldName);

    /**
     * Sets fields for which to retrieve parametric values
     *
     * @param fieldNames Fields for which to retrieve parametric values
     * @return the builder (for chaining)
     */
    B fieldNames(Collection<? extends FieldPath> fieldNames);

    /**
     * Clears collection of fields for which to retrieve parametric values
     *
     * @return the builder (for chaining)
     */
    B clearFieldNames();

    /**
     * @param start The first value to return
     * @return The builder (for chaining)
     */
    B start(Integer start);

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
    B range(ParametricFieldRange range);

    /**
     * Sets range information for numeric bucketing per field
     *
     * @param ranges Range information for numeric bucketing per field
     * @return the builder (for chaining)
     */
    B ranges(Collection<? extends ParametricFieldRange> ranges);

    /**
     * Clears collection of numeric range information
     *
     * @return the builder (for chaining)
     */
    B clearRanges();

    /**
     * Sets a wildcard restriction for values
     *
     * @param restriction Wildcard restriction for values
     * @return the builder (for chaining)
     */
    B valueRestriction(String restriction);

    /**
     * Sets wildcard restrictions for values
     *
     * @param restrictions Wildcard restrictions for values
     * @return the builder (for chaining)
     */
    B valueRestrictions(Collection<? extends String> restrictions);

    /**
     * Clears collection of wildcard restrictions for values
     *
     * @return the builder (for chaining)
     */
    B clearValueRestrictions();

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
