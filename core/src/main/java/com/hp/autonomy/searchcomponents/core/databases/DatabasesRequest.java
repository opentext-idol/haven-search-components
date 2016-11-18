/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.databases;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

/**
 * Options for interacting with {@link DatabasesService}
 */
@FunctionalInterface
public interface DatabasesRequest extends RequestObject<DatabasesRequest, DatabasesRequest.DatabasesRequestBuilder> {
    /**
     * Builder for {@link DatabasesRequest}
     */
    @FunctionalInterface
    interface DatabasesRequestBuilder extends RequestObject.RequestObjectBuilder<DatabasesRequest, DatabasesRequest.DatabasesRequestBuilder> {
    }
}
