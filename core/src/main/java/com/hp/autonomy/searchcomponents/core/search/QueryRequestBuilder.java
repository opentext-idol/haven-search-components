/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

/**
 * Builder for {@link QueryRequest}
 *
 * @param <R> The type of the request implementation
 * @param <Q> The type of the query restrictions object
 */
public interface QueryRequestBuilder<R extends QueryRequest<Q>, Q extends QueryRestrictions<?>, B extends QueryRequestBuilder<R, Q, B>>
        extends SearchRequestBuilder<R, Q, B>, RequestObjectBuilder<QueryRequest<Q>, QueryRequestBuilder<?, Q, ?>> {
    /**
     * Sets whether to apply auto-correction on results
     *
     * @param autoCorrect Whether to apply auto-correction on results
     * @return the builder (for chaining)
     */
    B autoCorrect(boolean autoCorrect);

    /**
     * Sets whether to apply intent-based-ranking on results
     *
     * @param intentBasedRanking Whether to apply intent-based-ranking on results
     * @return the builder (for chaining)
     */
    B intentBasedRanking(boolean intentBasedRanking);

    /**
     * Sets whether to query with QMS rules, without QMS rules, or for promotions
     *
     * @param queryType Whether to query with QMS rules, without QMS rules, or for promotions
     * @return the builder (for chaining)
     */
    B queryType(QueryRequest.QueryType queryType);

    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
