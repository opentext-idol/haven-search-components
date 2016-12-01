/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;

public abstract class ParametricRequestTest<Q extends QueryRestrictions<?>>
        extends RequestObjectTest<ParametricRequest<Q>, ParametricRequestBuilder<?, Q, ?>> {
    @Override
    protected String toStringContent() {
        return "fieldNames";
    }
}
