/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;

import java.io.Serializable;

public abstract class GetContentRequestIndexTest<S extends Serializable>
        extends RequestObjectTest<GetContentRequestIndex<S>, GetContentRequestIndexBuilder<?, S, ?>> {
    @Override
    protected String toStringContent() {
        return "references";
    }
}
