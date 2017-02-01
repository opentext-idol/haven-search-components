/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequestTest;

public class HodFieldsRequestTest extends FieldsRequestTest {
    @Override
    protected FieldsRequest constructObject() {
        return HodFieldsRequestImpl.builder()
                .database(ResourceName.WIKI_ENG)
                .maxValues(50)
                .build();
    }

    @Override
    protected String json() {
        return "{\"maxValues\": 50, \"database\": {\"domain\": \"PUBLIC_INDEXES\", \"name\": \"wiki_eng\"}}";
    }
}
