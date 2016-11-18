/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.SimpleRequestObjectTest;

import java.io.IOException;

public class TypedStateTokenTest extends SimpleRequestObjectTest<TypedStateToken> {
    @Override
    protected String json() throws IOException {
        return "{\"state_token\": \"0-ABC\", \"type\": \"QUERY\"}";
    }

    @Override
    protected TypedStateToken constructObject() {
        return new TypedStateToken("0-ABC", TypedStateToken.StateTokenType.QUERY);
    }

    @Override
    protected String toStringContent() {
        return "stateToken";
    }
}
