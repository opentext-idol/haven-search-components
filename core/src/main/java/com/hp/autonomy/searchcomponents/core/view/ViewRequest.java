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
     * @return How to view the document
     */
    ViewingPart getPart();

    /**
     * @return Links in the rendered document should be prefixed by this string
     */
    String getUrlPrefix();

    /**
     * @return Reference to the subdocument to view
     */
    String getSubDocRef();

}
