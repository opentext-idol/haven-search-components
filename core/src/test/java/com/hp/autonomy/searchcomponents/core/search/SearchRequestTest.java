/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;

public abstract class SearchRequestTest<Q extends QueryRestrictions<?>> extends RequestObjectTest<QueryRequest<Q>, QueryRequestBuilder<?, Q, ?>> {
    @Override
    protected String toStringContent() {
        return "queryRestrictions";
    }
}
