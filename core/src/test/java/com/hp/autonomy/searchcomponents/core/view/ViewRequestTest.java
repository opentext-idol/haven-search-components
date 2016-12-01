/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;

import java.io.Serializable;

public abstract class ViewRequestTest<S extends Serializable>
        extends RequestObjectTest<ViewRequest<S>, ViewRequestBuilder<?, S, ?>> {
    @Override
    protected String toStringContent() {
        return "documentReference";
    }
}
