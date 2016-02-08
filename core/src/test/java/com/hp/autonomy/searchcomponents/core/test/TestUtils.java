/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.test;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;

import java.io.Serializable;
import java.util.List;

public interface TestUtils<S extends Serializable> {
    List<S> getDatabases();

    QueryRestrictions<S> buildQueryRestrictions();
}
