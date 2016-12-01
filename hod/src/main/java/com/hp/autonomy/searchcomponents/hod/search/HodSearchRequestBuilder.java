/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.Sort;
import com.hp.autonomy.hod.client.api.textindex.query.search.Summary;
import com.hp.autonomy.searchcomponents.core.search.SearchRequestBuilder;

/**
 * Builder for {@link HodSearchRequest}
 */
public interface HodSearchRequestBuilder<R extends HodSearchRequest, B extends HodSearchRequestBuilder<R, B>> extends SearchRequestBuilder<R, HodQueryRestrictions, B> {
    /**
     * Sets the type of summary to generate
     *
     * @param summary The type of summary to generate
     * @return the builder (for chaining)
     */
    B summary(Summary summary);

    /**
     * Sets the criterion by which to order the results
     *
     * @param sort The criterion by which to order the results
     * @return the builder (for chaining)
     */
    B sort(Sort sort);

    /**
     * Sets what to display in the document result output
     *
     * @param print What to display in the document result output
     * @return the builder (for chaining)
     */
    B print(Print print);
}
