/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.databases;

import com.hp.autonomy.types.IdolDatabase;

import java.util.Set;

public interface DatabasesService<D extends IdolDatabase, R extends DatabasesRequest, E extends Exception> {
    Set<D> getDatabases(final R request) throws E;
}
