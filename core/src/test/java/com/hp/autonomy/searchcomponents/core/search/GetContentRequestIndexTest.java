/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hp.autonomy.searchcomponents.core.requests.SimpleRequestObjectTest;

import java.io.IOException;
import java.util.Collections;

public class GetContentRequestIndexTest extends SimpleRequestObjectTest<GetContentRequestIndex<String>> {
    @Override
    protected GetContentRequestIndex<String> constructObject() {
        return new GetContentRequestIndex<>("Database1", Collections.singleton("Reference1"));
    }

    @Override
    protected String json() throws IOException {
        return "{\"index\": \"Database1\", \"references\": [\"Reference1\"]}";
    }

    @Override
    protected Object readJson() throws IOException {
        return objectMapper.readValue(json(), new TypeReference<GetContentRequestIndex<String>>() {});
    }

    @Override
    protected String toStringContent() {
        return "references";
    }
}
