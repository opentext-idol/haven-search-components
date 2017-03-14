/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskSortParam;

import java.util.Set;

/**
 * Options for interacting with {@link AskAnswerServerService}
 */
@SuppressWarnings("WeakerAccess")
public interface AskAnswerServerRequest extends RequestObject<AskAnswerServerRequest, AskAnswerServerRequestBuilder> {
    /**
     * The query text
     *
     * @return The query text
     */
    String getText();

    /**
     * Sort criteria
     *
     * @return Sort criteria
     */
    AskSortParam getSort();

    /**
     * The systems behind AnswerServer to query
     *
     * @return The systems to query
     */
    Set<String> getSystemNames();

    /**
     * The first result to return (allows pagination)
     *
     * @return The index of the first result to return
     */
    Integer getFirstResult();

    /**
     * The maximum number of results to return
     *
     * @return The maximum number of results to return
     */
    Integer getMaxResults();

    /**
     * The score threshold for returned results
     *
     * @return The score threshold for returned results
     */
    Double getMinScore();
}
