/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

/**
 * Builder for {@link RelatedConceptsRequest}
 *
 * @param <R> The type of the request implementation
 * @param <Q> The type of the query restrictions object
 */
public interface RelatedConceptsRequestBuilder<R extends RelatedConceptsRequest<Q>, Q extends QueryRestrictions<?>, B extends RelatedConceptsRequestBuilder<R, Q, B>>
        extends RequestObjectBuilder<RelatedConceptsRequest<Q>, RelatedConceptsRequestBuilder<?, Q, ?>> {
    /**
     * Sets the maximum length of the query summary
     *
     * @param querySummaryLength The maximum length of the query summary
     * @return the builder (for chaining)
     */
    B querySummaryLength(int querySummaryLength);

    /**
     * Sets the maximum number of results to display
     *
     * @param maxResults The maximum number of results to display
     * @return the builder (for chaining)
     */
    B maxResults(Integer maxResults);

    /**
     * Sets the query restrictions to apply
     *
     * @param queryRestrictions The query restrictions to apply
     * @return the builder (for chaining)
     */
    B queryRestrictions(Q queryRestrictions);

    B queryType(QueryRequest.QueryType queryType);

    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
