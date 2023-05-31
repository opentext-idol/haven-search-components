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

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

import java.io.Serializable;

/**
 * Builder methods common to all request implementations
 *
 * @param <R> The type of the request implementation
 * @param <S> The type of the database identifier
 */
public interface ViewRequestBuilder<R extends ViewRequest<S>, S extends Serializable, B extends ViewRequestBuilder<R, S, B>>
        extends RequestObjectBuilder<ViewRequest<S>, ViewRequestBuilder<?, S, ?>> {
    /**
     * Sets the document reference
     *
     * @param documentReference The document reference
     * @return the builder (for chaining)
     */
    B documentReference(String documentReference);

    /**
     * Sets the database or index containing the document
     *
     * @param database The database or index containing the document
     * @return the builder (for chaining)
     */
    B database(S database);

    /**
     * Sets the text to highlight in the document
     *
     * @param highlightExpression Text to highlight in the document
     * @return the builder (for chaining)
     */
    B highlightExpression(String highlightExpression);

    /**
     * @param original Whether to retrieve the original file, without converting to HTML.
     * @return the builder (for chaining)
     */
    B original(boolean original);

    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
