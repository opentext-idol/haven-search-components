/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequestTest;

public class HodDatabasesRequestTest extends DatabasesRequestTest {
    @Override
    protected DatabasesRequest constructObject() {
        return HodDatabasesRequest.builder().publicIndexesEnabled(true).build();
    }

    @Override
    protected String json() {
        return "{\"publicIndexesEnabled\": true}";
    }

    @Override
    protected String toStringContent() {
        return "publicIndexesEnabled";
    }
}
