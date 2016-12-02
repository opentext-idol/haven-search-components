/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.databases;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

/**
 * Builder for {@link DatabasesRequest}
 */
@FunctionalInterface
public interface DatabasesRequestBuilder<R extends DatabasesRequest> extends RequestObjectBuilder<DatabasesRequest, DatabasesRequestBuilder<?>> {
    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
