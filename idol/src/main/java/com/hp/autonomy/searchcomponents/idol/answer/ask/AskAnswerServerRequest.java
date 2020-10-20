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

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskSortParam;

import java.util.Map;
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

    /**
     * The customization data, if any
     *
     * @return The customization data
     */
    String getCustomizationData();

    /**
     * The proxied parameters, if any.
     * (you have to configure [Server] AllowedQueryParameters in Answer Server to pass them through).
     * @return
     */
    Map<String, String> getProxiedParams();
}
