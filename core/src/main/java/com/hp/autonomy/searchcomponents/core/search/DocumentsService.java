/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service for performing queries against Idol documents
 *
 * @param <RQ> The {@link QueryRequest} implementation to use
 * @param <RS> The {@link SuggestRequest} implementation to use
 * @param <RC> The {@link GetContentRequest} implementation to use
 * @param <Q>  The type of the query restrictions object
 * @param <D>  The type of the document object returned in a standard query response
 * @param <E>  The checked exception thrown in the event of an error
 */
public interface DocumentsService<RQ extends QueryRequest<Q>, RS extends SuggestRequest<Q>, RC extends GetContentRequest<?>, Q extends QueryRestrictions<?>, D extends SearchResult, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String DOCUMENTS_SERVICE_BEAN_NAME = "documentsService";

    /**
     * The bean name of the default query request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String QUERY_REQUEST_BUILDER_BEAN_NAME = "queryRequestBuilder";

    /**
     * The bean name of the default suggest request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String SUGGEST_REQUEST_BUILDER_BEAN_NAME = "suggestRequestBuilder";

    /**
     * The bean name of the default getContent request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String GET_CONTENT_REQUEST_BUILDER_BEAN_NAME = "getContentRequestBuilder";

    /**
     * Start tag to add for highlighted words. This should be transformed by the client into something meaningful.
     */
    String HIGHLIGHT_START_TAG = "<HavenSearch-QueryText-Placeholder>";

    /**
     * End tag to add for highlighted words. This should be transformed by the client into something meaningful.
     */
    String HIGHLIGHT_END_TAG = "</HavenSearch-QueryText-Placeholder>";

    /**
     * Standard query against Idol
     *
     * @param queryRequest Query restrictions and output modifiers
     * @return The search results
     * @throws E The error thrown in the event of the platform returning an error response
     */
    Documents<D> queryTextIndex(RQ queryRequest) throws E;

    /**
     * Idol suggest request
     *
     * @param suggestRequest Query restrictions and output modifiers
     * @return The suggest results
     * @throws E The error thrown in the event of the platform returning an error response
     */
    Documents<D> findSimilar(RS suggestRequest) throws E;

    /**
     * Retrieves the document fields and content for the specified resources
     *
     * @param request The details of the documents to retrieve
     * @return The retrieved document information
     * @throws E The error thrown in the event of the platform returning an error response
     */
    List<D> getDocumentContent(RC request) throws E;

    /**
     * Retrieves a state token for a given query
     *
     * @param queryRestrictions The query restrictions
     * @param maxResults        The number of query results
     * @param queryType         How to perform the query
     * @param promotions        Whether to query for promotions
     * @return The state token
     * @throws E The error thrown in the event of the platform returning an error response
     */
    String getStateToken(
        Q queryRestrictions, int maxResults, QueryRequest.QueryType queryType, boolean promotions
    ) throws E;

    /**
     * Retrieves a state token and result count for a given query
     *
     * @param queryRestrictions The query restrictions
     * @param maxResults        The number of query results
     * @param queryType         How to perform the query
     * @param promotions        Whether to query for promotions
     * @return The state token
     * @throws E The error thrown in the event of the platform returning an error response
     */
    StateTokenAndResultCount getStateTokenAndResultCount(
        Q queryRestrictions, int maxResults, QueryRequest.QueryType queryType, boolean promotions
    ) throws E;
}
