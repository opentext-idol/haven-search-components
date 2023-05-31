/*
 * Copyright 2015 Open Text.
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

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.idol.actions.query.QuerySummaryElement;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service for retrieving conceptually similar results to a search
 *
 * @param <R> The request type to use
 * @param <T> The related concept response type
 * @param <Q> The type of the query restrictions object
 * @param <E> The checked exception thrown in the event of an error
 */
@FunctionalInterface
public interface RelatedConceptsService<R extends RelatedConceptsRequest<Q>, T extends QuerySummaryElement, Q extends QueryRestrictions<?>, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String RELATED_CONCEPTS_SERVICE_BEAN_NAME = "relatedConceptsService";


    /**
     * The bean name of the default request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME = "relatedConceptsRequestBuilder";

    /**
     * Retrieves conceptually similar results for a search
     *
     * @param relatedConceptsRequest Query restrictions
     * @return The related concepts
     * @throws E The error thrown in the event of the platform returning an error response
     */
    List<T> findRelatedConcepts(R relatedConceptsRequest) throws E;
}
