/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;

import java.io.IOException;
import java.util.Collections;

public class GetContentRequestTest extends RequestObjectTest<GetContentRequest<String>, GetContentRequest.GetContentRequestBuilder<String>> {
    @Override
    protected GetContentRequest<String> constructObject() {
        return GetContentRequest.<String>builder()
                .indexAndReferences(new GetContentRequestIndex<>("Database1", Collections.singleton("Reference1")))
                .build();
    }

    @Override
    protected String json() throws IOException {
        return "{\"indexesAndReferences\": [{\"index\": \"Database1\", \"references\": [\"Reference1\"]}]}";
    }

    @Override
    protected Object readJson() throws IOException {
        return objectMapper.readValue(json(), new TypeReference<GetContentRequest<String>>() {});
    }

    @Override
    protected String toStringContent() {
        return "references";
    }
}
