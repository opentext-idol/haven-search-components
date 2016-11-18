/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

import java.io.Serializable;

/**
 * Options for interacting with {@link RelatedConceptsService}
 *
 * @param <S> The type of the database identifier
 */
public interface RelatedConceptsRequest<S extends Serializable>
        extends RequestObject<RelatedConceptsRequest<S>, RelatedConceptsRequest.RelatedConceptsRequestBuilder<?, S>> {
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
    QueryRestrictions<S> getQueryRestrictions();

    interface RelatedConceptsRequestBuilder<R extends RelatedConceptsRequest<S>, S extends Serializable>
            extends RequestObject.RequestObjectBuilder<RelatedConceptsRequest<S>, RelatedConceptsRequest.RelatedConceptsRequestBuilder<?, S>> {
        /**
         * Sets the maximum length of the query summary
         *
         * @param querySummaryLength The maximum length of the query summary
         * @return the builder (for chaining)
         */
        RelatedConceptsRequestBuilder<R, S> querySummaryLength(int querySummaryLength);

        /**
         * Sets the maximum number of results to display
         *
         * @param maxResults The maximum number of results to display
         * @return the builder (for chaining)
         */
        RelatedConceptsRequestBuilder<R, S> maxResults(Integer maxResults);

        /**
         * Sets the query restrictions to apply
         *
         * @param queryRestrictions The query restrictions to apply
         * @return the builder (for chaining)
         */
        RelatedConceptsRequestBuilder<R, S> queryRestrictions(QueryRestrictions<S> queryRestrictions);

        /**
         * {@inheritDoc}
         */
        @Override
        R build();
    }
}
