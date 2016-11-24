/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;

/**
 * HoD extension to {@link DocumentsService}
 */
public interface HodDocumentsService extends DocumentsService<ResourceIdentifier, HodSearchResult, HodErrorException> {
    /**
     * HoD limits max results to 2500
     */
    int HOD_MAX_RESULTS = 2500;
}
