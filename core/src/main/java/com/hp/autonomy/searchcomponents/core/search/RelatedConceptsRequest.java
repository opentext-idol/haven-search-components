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
