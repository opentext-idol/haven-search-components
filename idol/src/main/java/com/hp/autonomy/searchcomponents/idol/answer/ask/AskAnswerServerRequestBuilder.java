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

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

import java.util.Collection;
import java.util.Map;

/**
 * Builder for {@link AskAnswerServerRequest}
 */
@SuppressWarnings({"unused", "SameParameterValue"})
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
     * Sets the first result to return
     *
     * @param firstResult The index of the first result to return
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder firstResult(Integer firstResult);

    /**
     * Sets the maximum number of results to return
     *
     * @param maxResults The maximum number of results to return
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder maxResults(Integer maxResults);

    /**
     * Sets the customization data, if any
     *
     * @param customizationData The customization data
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder customizationData(String customizationData);

    /**
     * Sets any proxied parameters, if any.
     * (you have to configure [Server] AllowedQueryParameters in Answer Server to pass them through).
     *
     * @param proxiedParams Map of proxied parameters
     * @return the builder (for chaining)
     */
    AskAnswerServerRequestBuilder proxiedParams(Map<? extends String, ? extends String> proxiedParams);
}
