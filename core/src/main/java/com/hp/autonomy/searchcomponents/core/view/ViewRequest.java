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
public interface ViewRequest<S extends Serializable>
        extends RequestObject<ViewRequest<S>, ViewRequest.ViewRequestBuilder<?, S>> {
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
     * Builder methods common to all request implementations
     *
     * @param <R> The type of the request implementation
     * @param <S> The type of the database identifier
     */
    interface ViewRequestBuilder<R extends ViewRequest<S>, S extends Serializable>
            extends RequestObject.RequestObjectBuilder<ViewRequest<S>, ViewRequestBuilder<?, S>> {
        /**
         * Sets the document reference
         *
         * @param documentReference The document reference
         * @return the builder (for chaining)
         */
        ViewRequestBuilder<R, S> documentReference(String documentReference);

        /**
         * Sets the database or index containing the document
         *
         * @param database The database or index containing the document
         * @return the builder (for chaining)
         */
        ViewRequestBuilder<R, S> database(S database);

        /**
         * Sets the text to highlight in the document
         *
         * @param highlightExpression Text to highlight in the document
         * @return the builder (for chaining)
         */
        ViewRequestBuilder<R, S> highlightExpression(String highlightExpression);

        /**
         * {@inheritDoc}
         */
        @Override
        R build();
    }
}
