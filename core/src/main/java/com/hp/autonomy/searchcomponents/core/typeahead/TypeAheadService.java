/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
