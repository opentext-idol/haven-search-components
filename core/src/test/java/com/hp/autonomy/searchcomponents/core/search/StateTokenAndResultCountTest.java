/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
