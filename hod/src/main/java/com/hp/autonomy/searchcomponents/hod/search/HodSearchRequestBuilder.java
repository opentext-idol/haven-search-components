/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.search.SearchRequestBuilder;

/**
 * Builder for {@link HodSearchRequest}
 */
public interface HodSearchRequestBuilder<R extends HodSearchRequest, B extends HodSearchRequestBuilder<R, B>> extends SearchRequestBuilder<R, HodQueryRestrictions, B> {
}
