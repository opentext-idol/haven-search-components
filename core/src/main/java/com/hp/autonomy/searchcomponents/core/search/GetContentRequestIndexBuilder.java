/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.Collection;

/**
 * Builder for {@link GetContentRequestIndex}
 */
@SuppressWarnings("unused")
public interface GetContentRequestIndexBuilder<T extends GetContentRequestIndex<S>, S extends Serializable, B extends GetContentRequestIndexBuilder<T, S, B>>
        extends RequestObjectBuilder<GetContentRequestIndex<S>, GetContentRequestIndexBuilder<?, S, ?>> {
    /**
     * The bean name of the default builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME = "getContentRequestIndexBuilder";

    /**
     * Sets the database in which the references are located
     *
     * @param index The database in which the references are located
     * @return the builder (for chaining)
     */
    B index(S index);

    /**
     * Sets the references of the documents whose content is to be queried
     *
     * @param references The references of the documents whose content is to be queried
     * @return the builder (for chaining)
     */
    B references(Collection<? extends String> references);

    /**
     * Sets a references of a document whose content is to be queried
     *
     * @param reference A references of a document whose content is to be queried
     * @return the builder (for chaining)
     */
    B reference(String reference);

    /**
     * Clear the collection of document references
     *
     * @return the builder (for chaining)
     */
    B clearReferences();

    /**
     * {@inheritDoc}
     */
    @Override
    T build();
}
