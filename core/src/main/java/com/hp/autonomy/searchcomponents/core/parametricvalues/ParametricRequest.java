/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Options for interacting with {@link ParametricValuesService}
 *
 * @param <S> The type of the database identifier
 */
public interface ParametricRequest<S extends Serializable>
        extends RequestObject<ParametricRequest<S>, ParametricRequest.ParametricRequestBuilder<?, S>> {
    /**
     * Fields for which to retrieve parametric values
     *
     * @return Idol field names
     */
    List<String> getFieldNames();

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
    QueryRestrictions<S> getQueryRestrictions();

    /**
     * Whether to apply QMS rules
     *
     * @return whether to apply QMS rules
     */
    boolean isModified();

    /**
     * Builder methods common to all request implementations
     *
     * @param <P> The type of the request implementation
     * @param <S> The type of the database identifier
     */
    @SuppressWarnings("unused")
    interface ParametricRequestBuilder<P extends ParametricRequest<S>, S extends Serializable>
            extends RequestObject.RequestObjectBuilder<ParametricRequest<S>, ParametricRequestBuilder<?, S>> {
        /**
         * Sets a field for which to retrieve parametric values
         *
         * @param fieldName Field for which to retrieve parametric values
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> fieldName(String fieldName);

        /**
         * Sets fields for which to retrieve parametric values
         *
         * @param fieldNames Fields for which to retrieve parametric values
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> fieldNames(Collection<? extends String> fieldNames);

        /**
         * Clears collection of fields for which to retrieve parametric values
         *
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> clearFieldNames();

        /**
         * Sets max results to return
         *
         * @param maxValues Max results to return
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> maxValues(Integer maxValues);

        /**
         * Sets the criterion by which to sort
         *
         * @param sort The criterion by which to sort
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> sort(SortParam sort);

        /**
         * Sets Range information for numeric bucketing for a single field
         *
         * @param range Range information for numeric bucketing for a single field
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> range(Range range);

        /**
         * Sets range information for numeric bucketing per field
         *
         * @param ranges Range information for numeric bucketing per field
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> ranges(Collection<? extends Range> ranges);

        /**
         * Clears collection of numeric range information
         *
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> clearRanges();

        /**
         * Sets the restrictions on the underlying query
         *
         * @param queryRestrictions The restrictions on the underlying query
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> queryRestrictions(QueryRestrictions<S> queryRestrictions);

        /**
         * Sets whether to apply QMS rules
         *
         * @param modified Whether to apply QMS rules
         * @return the builder (for chaining)
         */
        ParametricRequestBuilder<P, S> modified(boolean modified);

        /**
         * {@inheritDoc}
         */
        @Override
        P build();
    }
}
