/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Service for viewing documents as html
 *
 * @param <S> The type of the database identifier
 * @param <E> The checked exception thrown in the event of an error
 */
public interface ViewServerService<S extends Serializable, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String VIEW_SERVER_SERVICE_BEAN_NAME = "viewServerService";

    /**
     * Start tag to add for highlighted words.
     */
    String HIGHLIGHT_START_TAG = "<span class='haven-search-view-document-highlighting'>";

    /**
     * End tag to add for highlighted words.
     */
    String HIGHLIGHT_END_TAG = "</span>";

    /**
     * View the document with the given reference in the given index, writing the output to the given output stream.
     *
     * @param documentReference   The document reference
     * @param database            The database or index containing the document
     * @param highlightExpression Text to highlight in the document
     * @param outputStream        The output stream to write the viewed document to
     * @throws E The error thrown in the event of the platform returning an error response
     */
    void viewDocument(String documentReference, S database, String highlightExpression, OutputStream outputStream) throws E, IOException;

    /**
     * View a static content promotion, writing the output to the given output stream.
     *
     * @param documentReference The reference of the search result created by the promotion
     * @param outputStream      The output stream to write the viewed document to
     * @throws IOException
     * @throws E           The error thrown in the event of the platform returning an error response
     */
    void viewStaticContentPromotion(String documentReference, OutputStream outputStream) throws IOException, E;
}
