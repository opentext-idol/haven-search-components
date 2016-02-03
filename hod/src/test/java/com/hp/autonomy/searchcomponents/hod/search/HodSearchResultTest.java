/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SearchResultTest;

public class HodSearchResultTest extends SearchResultTest {
    @Override
    protected SearchResult buildSearchResult(final String reference) {
        return new HodSearchResult.Builder().setReference(reference).build();
    }
}
