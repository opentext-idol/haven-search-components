/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestTest;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndex;
import org.junit.Before;

import java.io.IOException;
import java.util.Collections;

public class IdolGetContentRequestTest extends GetContentRequestTest<IdolGetContentRequestIndex> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(GetContentRequestIndex.class, IdolGetContentRequestIndexMixin.class);
    }

    @Override
    protected IdolGetContentRequest constructObject() {
        return IdolGetContentRequestImpl.builder()
                .indexAndReferences(new IdolGetContentRequestIndexImpl("Database1", Collections.singleton("Reference1")))
                .build();
    }

    @Override
    protected String json() throws IOException {
        return "{\"indexesAndReferences\": [{\"index\": \"Database1\", \"references\": [\"Reference1\"]}]}";
    }
}
