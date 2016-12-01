/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.search.QueryRequest;

/**
 * Options for interacting with {@link HodDocumentsService#queryTextIndex(QueryRequest)}
 */
public interface HodQueryRequest extends HodSearchRequest, QueryRequest<HodQueryRestrictions> {
    /**
     * {@inheritDoc}
     */
    @Override
    HodQueryRequestBuilder toBuilder();
}
