/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;

public abstract class FieldsRequestTest extends RequestObjectTest<FieldsRequest, FieldsRequest.FieldsRequestBuilder<?>> {
    @Override
    protected String toStringContent() {
        return "maxValues";
    }
}
