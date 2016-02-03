/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SearchResultTest;

public class IdolSearchResultTest extends SearchResultTest {
    @Override
    protected SearchResult buildSearchResult(final String reference) {
        return new IdolSearchResult.Builder().setReference(reference).build();
    }
}
