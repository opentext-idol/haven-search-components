/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequestTest;

public class IdolDatabasesRequestTest extends DatabasesRequestTest {
    @Override
    protected DatabasesRequest constructObject() {
        return IdolDatabasesRequestImpl.builder().build();
    }

    @Override
    protected String json() {
        return "{}";
    }

    @Override
    protected String toStringContent() {
        return "";
    }
}
