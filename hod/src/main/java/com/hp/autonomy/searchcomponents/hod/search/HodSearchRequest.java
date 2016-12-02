/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;

/**
 * HoD extension of {@link SearchRequest}
 */
@SuppressWarnings("WeakerAccess")
public interface HodSearchRequest extends SearchRequest<HodQueryRestrictions> {
    Print DEFAULT_PRINT = Print.fields;
}
