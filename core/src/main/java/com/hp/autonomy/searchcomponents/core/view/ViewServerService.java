/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public interface ViewServerService<S extends Serializable, E extends Exception> {

    String HIGHLIGHT_START_TAG = "<span class='haven-search-view-document-highlighting'>";
    String HIGHLIGHT_END_TAG = "</span>";

    /**
     * View the document with the given reference in the given index, writing the output to the given output stream.
     *
     * @param documentReference   The document reference
     * @param database            The database or index containing the document
     * @param highlightExpression Text to highlight in the document
     * @param outputStream        The output stream to write the viewed document to
     * @throws E any error
     */
    void viewDocument(String documentReference, S database, String highlightExpression, OutputStream outputStream) throws E, IOException;

    /**
     * View a static content promotion, writing the output to the given output stream.
     *
     * @param documentReference The reference of the search result created by the promotion
     * @param outputStream      The output stream to write the viewed document to
     * @throws IOException
     * @throws E           any error
     */
    void viewStaticContentPromotion(String documentReference, OutputStream outputStream) throws IOException, E;
}
