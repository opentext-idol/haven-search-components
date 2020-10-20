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

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

/**
 * Options for interacting with {@link DocumentsService#queryTextIndex(QueryRequest)}
 *
 * @param <Q> The type of the query restrictions object
 */
public interface QueryRequest<Q extends QueryRestrictions<?>>
        extends SearchRequest<Q>, RequestObject<QueryRequest<Q>, QueryRequestBuilder<?, Q, ?>> {
    /**
     * Whether to apply auto-correction on results
     *
     * @return Whether to apply auto-correction on results
     */
    boolean isAutoCorrect();

    /**
     * Whether to apply intent-based ranking on results
     * @return whether we're using intent-based ranking
     */
    boolean isIntentBasedRanking();

    /**
     * Whether to query with QMS rules, without QMS rules, or for promotions
     *
     * @return Whether to query with QMS rules, without QMS rules, or for promotions
     */
    QueryType getQueryType();

    /**
     * The type of Idol Query to perform
     */
    enum QueryType {
        RAW, MODIFIED, PROMOTIONS
    }
}
