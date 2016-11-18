/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.databases;

import com.hp.autonomy.types.IdolDatabase;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Set;

/**
 * Service for retrieving information relating to platform databases
 *
 * @param <D> The type representing a platform database
 * @param <R> The request type to use
 * @param <E> The checked exception thrown in the event of an error
 */
@FunctionalInterface
public interface DatabasesService<D extends IdolDatabase, R extends DatabasesRequest, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String DATABASES_SERVICE_BEAN_NAME = "databasesService";

    /**
     * Retrieves the details of the databases in the platform
     *
     * @param request options
     * @return the database details
     * @throws E the error thrown in the event of the platform returning an error response
     */
    Set<D> getDatabases(final R request) throws E;
}
