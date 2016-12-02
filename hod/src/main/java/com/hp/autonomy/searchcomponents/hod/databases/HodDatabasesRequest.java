/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;

/**
 * Options for interacting with {@link HodDatabasesService}
 */
public interface HodDatabasesRequest extends DatabasesRequest {
    /**
     * Whether to return public indexes as well as private indexes
     *
     * @return Whether to return public indexes as well as private indexes
     */
    boolean isPublicIndexesEnabled();

    /**
     * {@inheritDoc}
     */
    @Override
    HodDatabasesRequestBuilder toBuilder();
}
