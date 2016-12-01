/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.QueryRequest;

/**
 * Options for interacting with {@link IdolDocumentsService#queryTextIndex(QueryRequest)}
 */
public interface IdolQueryRequest extends IdolSearchRequest, QueryRequest<IdolQueryRestrictions> {
    /**
     * {@inheritDoc}
     */
    @Override
    IdolQueryRequestBuilder toBuilder();
}
