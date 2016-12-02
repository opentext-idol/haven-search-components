/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

import java.util.Collection;

/**
 * Builder for {@link AskAnswerServerRequest}
 */
@SuppressWarnings("unused")
public interface AskAnswerServerRequestBuilder extends RequestObjectBuilder<AskAnswerServerRequest, AskAnswerServerRequestBuilder> {
    /**
     * Sets the query text
     *
     * @param text The query text
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder text(String text);

    /**
     * Sets the systems to query
     *
     * @param systemNames The systems to query
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder systemNames(Collection<? extends String> systemNames);

    /**
     * Sets a systems to query
     *
     * @param systemName A system to query
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder systemName(String systemName);

    /**
     * Clears the set of systems to query
     *
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder clearSystemNames();

    /**
     * Sets the maximum number of results to return
     *
     * @param maxResults The maximum number of results to return
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder maxResults(Integer maxResults);
}
