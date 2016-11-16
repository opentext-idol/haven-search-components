/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;

import java.io.Serializable;

public abstract class QueryRestrictionsTest<S extends Serializable> extends RequestObjectTest<QueryRestrictions<S>, QueryRestrictions.QueryRestrictionsBuilder<?, S>> {
    @Override
    protected String toStringContent() {
        return "queryText";
    }
}
