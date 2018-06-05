/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

/**
 * Options for interacting with {@link RelatedConceptsService}
 *
 * @param <Q> The type of the query restrictions object
 */
public interface RelatedConceptsRequest<Q extends QueryRestrictions<?>>
        extends RequestObject<RelatedConceptsRequest<Q>, RelatedConceptsRequestBuilder<?, Q, ?>> {
    /**
     * Maximum length of the query summary.
     *
     * @return The maximum length of the query summary
     */
    int getQuerySummaryLength();

    /**
     * Maximum number of results to display
     *
     * @return The maximum number of results to display
     */
    Integer getMaxResults();

    /**
     * The query restrictions to apply
     *
     * @return The query restrictions to apply
     */
    Q getQueryRestrictions();

    QueryRequest.QueryType getQueryType();
}
