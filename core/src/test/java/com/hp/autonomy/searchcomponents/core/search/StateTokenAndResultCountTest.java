/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.SerializableObjectTest;

public class StateTokenAndResultCountTest extends SerializableObjectTest<StateTokenAndResultCount> {
    @Override
    protected StateTokenAndResultCount constructObject() {
        return new StateTokenAndResultCount(new TypedStateToken("0-ABC", TypedStateToken.StateTokenType.QUERY), 5);
    }

    @Override
    protected String toStringContent() {
        return "stateToken";
    }
}
