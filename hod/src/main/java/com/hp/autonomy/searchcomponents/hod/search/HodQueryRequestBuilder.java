/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.search.QueryRequestBuilder;

/**
 * Builder for {@link HodQueryRequest}
 */
public interface HodQueryRequestBuilder extends HodSearchRequestBuilder<HodQueryRequest, HodQueryRequestBuilder>, QueryRequestBuilder<HodQueryRequest, HodQueryRestrictions, HodQueryRequestBuilder> {
}
