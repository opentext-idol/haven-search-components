/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

import java.util.Set;

/**
 * Options for interacting with {@link DocumentsService#getDocumentContent(GetContentRequest)}
 *
 * @param <T> The type of the per-database information
 */
public interface GetContentRequest<T extends GetContentRequestIndex<?>>
        extends RequestObject<GetContentRequest<T>, GetContentRequestBuilder<?, T, ?>> {
    /**
     * The references (per database) for which to retrieve content
     *
     * @return The references (per database) for which to retrieve content
     */
    Set<T> getIndexesAndReferences();
}
