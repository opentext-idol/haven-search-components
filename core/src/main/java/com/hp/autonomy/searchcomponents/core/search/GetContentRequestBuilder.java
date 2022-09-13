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
     * @param referenceField The field used to store document references (must be a reference type
     *                       field)
     * @return the builder (for chaining)
     */
    B referenceField(String referenceField);

    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
