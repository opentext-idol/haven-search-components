/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

import java.io.Serializable;

/**
 * Options for interacting with {@link ViewServerService}
 *
 * @param <S> The type of the database identifier
 */
public interface ViewRequest<S extends Serializable> extends RequestObject<ViewRequest<S>, ViewRequestBuilder<?, S, ?>> {
    /**
     * The document reference
     *
     * @return The document reference
     */
    String getDocumentReference();

    /**
     * The database or index containing the document
     *
     * @return The database or index containing the document
     */
    S getDatabase();

    /**
     * Text to highlight in the document
     *
     * @return Text to highlight in the document
     */
    String getHighlightExpression();

    /**
     * @return Whether to retrieve the original file, without converting to HTML.
     */
    boolean isOriginal();

}
