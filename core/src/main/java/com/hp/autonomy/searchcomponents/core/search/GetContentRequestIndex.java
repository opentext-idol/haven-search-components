/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

import java.io.Serializable;
import java.util.Set;

/**
 * Option for interacting with {@link DocumentsService#getDocumentContent(GetContentRequest)}
 *
 * @param <S> The type of the database identifier
 */
public interface GetContentRequestIndex<S extends Serializable> extends RequestObject<GetContentRequestIndex<S>, GetContentRequestIndexBuilder<?, S, ?>> {
    /**
     * The database in which the references are located
     *
     * @return The database in which the references are located
     */
    S getIndex();

    /**
     * The references of the documents whose content is to be queried
     *
     * @return The references of the documents whose content is to be queried
     */
    Set<String> getReferences();
}
