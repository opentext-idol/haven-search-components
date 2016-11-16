/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.List;

/**
 * Service for performing queries against Idol documents
 *
 * @param <S> The type of the database identifier
 * @param <D> The type of the document object returned in a standard query response
 * @param <E> The checked exception thrown in the event of an error
 */
public interface DocumentsService<S extends Serializable, D extends SearchResult, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String DOCUMENTS_SERVICE_BEAN_NAME = "documentsService";

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
     * @param searchRequest Query restrictions and output modifiers
     * @return The search results
     * @throws E The error thrown in the event of the platform returning an error response
     */
    Documents<D> queryTextIndex(SearchRequest<S> searchRequest) throws E;

    /**
     * Idol suggest request
     *
     * @param suggestRequest Query restrictions and output modifiers
     * @return The suggest results
     * @throws E The error thrown in the event of the platform returning an error response
     */
    Documents<D> findSimilar(SuggestRequest<S> suggestRequest) throws E;

    /**
     * Retrieves the document fields and content for the specified resources
     *
     * @param request The details of the documents to retrieve
     * @return The retrieved document information
     * @throws E The error thrown in the event of the platform returning an error response
     */
    List<D> getDocumentContent(GetContentRequest<S> request) throws E;

    /**
     * Retrieves a state token for a given query
     *
     * @param queryRestrictions The query restrictions
     * @param maxResults        The number of query results
     * @param promotions        Whether to query for promotions
     * @return The state token
     * @throws E The error thrown in the event of the platform returning an error response
     */
    String getStateToken(QueryRestrictions<S> queryRestrictions, int maxResults, boolean promotions) throws E;

    /**
     * Retrieves a state token and result count for a given query
     *
     * @param queryRestrictions The query restrictions
     * @param maxResults        The number of query results
     * @param promotions        Whether to query for promotions
     * @return The state token
     * @throws E The error thrown in the event of the platform returning an error response
     */
    StateTokenAndResultCount getStateTokenAndResultCount(QueryRestrictions<S> queryRestrictions, int maxResults, boolean promotions) throws E;
}
