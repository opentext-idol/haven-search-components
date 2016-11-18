/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;

import java.io.Serializable;

public abstract class ParametricRequestTest<S extends Serializable>
        extends RequestObjectTest<ParametricRequest<S>, ParametricRequest.ParametricRequestBuilder<?, S>> {
    @Override
    protected String toStringContent() {
        return "fieldNames";
    }
}
