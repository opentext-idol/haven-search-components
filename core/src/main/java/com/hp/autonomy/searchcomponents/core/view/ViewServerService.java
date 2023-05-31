/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.view;

import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Service for viewing documents as html
 *
 * @param <R> The request type to use
 * @param <S> The type of the database identifier
 * @param <E> The checked exception thrown in the event of an error
 */
public interface ViewServerService<R extends ViewRequest<S>, S extends Serializable, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String VIEW_SERVER_SERVICE_BEAN_NAME = "viewServerService";

    /**
     * The bean name of the default request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String VIEW_REQUEST_BUILDER_BEAN_NAME = "viewRequestBuilder";

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
     * @param request      options
     * @param outputStream The output stream to write the viewed document to
     * @throws IOException Stream error
     * @throws E           The error thrown in the event of the platform returning an error response
     */
    void viewDocument(R request, OutputStream outputStream) throws E, IOException;

    /**
     * View a static content promotion, writing the output to the given output stream.
     *
     * @param documentReference The reference of the search result created by the promotion
     * @param outputStream      The output stream to write the viewed document to
     * @throws IOException Stream error
     * @throws E           The error thrown in the event of the platform returning an error response
     */
    void viewStaticContentPromotion(String documentReference, OutputStream outputStream) throws IOException, E;
}
