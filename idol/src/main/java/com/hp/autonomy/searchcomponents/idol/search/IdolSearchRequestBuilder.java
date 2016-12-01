/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.SearchRequestBuilder;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;

/**
 * Builder for {@link IdolSearchRequest}
 */
public interface IdolSearchRequestBuilder<R extends IdolSearchRequest, B extends IdolSearchRequestBuilder<R, B>> extends SearchRequestBuilder<R, IdolQueryRestrictions, B> {
    /**
     * Sets the type of summary to generate
     *
     * @param summary The type of summary to generate
     * @return the builder (for chaining)
     */
    B summary(SummaryParam summary);

    /**
     * Sets the criterion by which to order the results
     *
     * @param sort The criterion by which to order the results
     * @return the builder (for chaining)
     */
    B sort(SortParam sort);

    /**
     * Sets what to display in the document result output
     *
     * @param print What to display in the document result output
     * @return the builder (for chaining)
     */
    B print(PrintParam print);
}
