/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;

/**
 * Options for interacting with {@link HodRelatedConceptsService}
 */
public interface HodRelatedConceptsRequest extends RelatedConceptsRequest<HodQueryRestrictions> {
    /**
     * {@inheritDoc}
     */
    @Override
    HodRelatedConceptsRequestBuilder toBuilder();
}
