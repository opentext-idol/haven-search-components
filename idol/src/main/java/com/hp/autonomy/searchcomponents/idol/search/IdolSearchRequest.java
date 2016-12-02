/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;

/**
 * Idol extension of {@link SearchRequest}
 */
public interface IdolSearchRequest extends SearchRequest<IdolQueryRestrictions> {
    PrintParam DEFAULT_PRINT = PrintParam.Fields;
}
