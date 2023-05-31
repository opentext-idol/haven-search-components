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

package com.hp.autonomy.searchcomponents.core.typeahead;

import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service for providing suggestions for auto-completion of query text
 *
 * @param <E> The checked exception thrown in the event of an error
 */
@FunctionalInterface
public interface TypeAheadService<E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String TYPE_AHEAD_SERVICE_BEAN_NAME = "typeAheadService";

    /**
     * Generates auto-complete suggestions
     *
     * @param text The text so far
     * @return The suggestions
     * @throws E The error thrown in the event of the platform returning an error response
     */
    List<String> getSuggestions(String text) throws E;
}
