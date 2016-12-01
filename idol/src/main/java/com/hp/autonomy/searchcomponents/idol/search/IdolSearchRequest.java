/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;

/**
 * Idol extension of {@link SearchRequest}
 */
public interface IdolSearchRequest extends SearchRequest<IdolQueryRestrictions> {
    PrintParam DEFAULT_PRINT = PrintParam.Fields;

    /**
     * The type of summary to generate
     *
     * @return The type of summary to generate
     */
    SummaryParam getSummary();

    /**
     * The criterion by which to order the results
     *
     * @return The criterion by which to order the results
     */
    SortParam getSort();

    /**
     * What to display in the document result output
     *
     * @return What to display in the document result output
     */
    PrintParam getPrint();
}
