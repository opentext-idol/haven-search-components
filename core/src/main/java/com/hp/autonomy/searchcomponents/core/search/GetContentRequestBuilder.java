/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

import java.util.Collection;

@SuppressWarnings("unused")
public interface GetContentRequestBuilder<R extends GetContentRequest<T>, T extends GetContentRequestIndex<?>, B extends GetContentRequestBuilder<R, T, B>>
        extends RequestObjectBuilder<GetContentRequest<T>, GetContentRequestBuilder<?, T, ?>> {
    /**
     * Sets The references (per database) for which to retrieve content
     *
     * @param indexesAndReferences The references (per database) for which to retrieve content
     * @return the builder (for chaining)
     */
    B indexesAndReferences(Collection<? extends T> indexesAndReferences);

    /**
     * Sets references within a single database for which to retrieve content
     *
     * @param indexAndReferences References within a single database for which to retrieve content
     * @return the builder (for chaining)
     */
    B indexAndReferences(T indexAndReferences);

    /**
     * Clears the set of references for which to retrieve content
     *
     * @return the builder (for chaining)
     */
    B clearIndexesAndReferences();

    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
