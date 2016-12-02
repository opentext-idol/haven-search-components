/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.SearchRequestBuilder;

/**
 * Builder for {@link IdolSearchRequest}
 */
public interface IdolSearchRequestBuilder<R extends IdolSearchRequest, B extends IdolSearchRequestBuilder<R, B>> extends SearchRequestBuilder<R, IdolQueryRestrictions, B> {
}
