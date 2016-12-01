/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.test;

import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;

public interface TestUtils<Q extends QueryRestrictions<?>> {
    String CUSTOMISATION_TEST_ID = "customisation-test";

    Q buildQueryRestrictions();

    <RC extends GetContentRequest<?>> RC buildGetContentRequest(String reference);
}
