/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequestTest;

public class IdolFieldsRequestTest extends FieldsRequestTest {
    @Override
    protected FieldsRequest constructObject() {
        return IdolFieldsRequestImpl.builder()
                .maxValues(50)
                .build();
    }

    @Override
    protected String json() {
        return "{\"maxValues\": 50}";
    }
}
