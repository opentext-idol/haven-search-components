/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequestBuilder;

/**
 * Builder for {@link HodDatabasesRequest}
 */
public interface HodDatabasesRequestBuilder extends DatabasesRequestBuilder<HodDatabasesRequest> {
    /**
     * Sets whether to return public indexes as well as private indexes
     *
     * @param publicIndexesEnabled Whether to return public indexes as well as private indexes
     * @return The builder (for chaining)
     */
    HodDatabasesRequestBuilder publicIndexesEnabled(boolean publicIndexesEnabled);
}