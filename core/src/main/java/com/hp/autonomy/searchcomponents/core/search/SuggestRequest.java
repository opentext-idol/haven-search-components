/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

/**
 * Options for interacting with {@link DocumentsService#findSimilar(SuggestRequest)}
 *
 * @param <Q> The type of the query restrictions object
 */
public interface SuggestRequest<Q extends QueryRestrictions<?>>
        extends SearchRequest<Q>, RequestObject<SuggestRequest<Q>, SuggestRequestBuilder<?, Q, ?>> {
    /**
     * The document reference to use as the basis for the suggest
     *
     * @return The document reference to use as the basis for the suggest
     */
    String getReference();
}
