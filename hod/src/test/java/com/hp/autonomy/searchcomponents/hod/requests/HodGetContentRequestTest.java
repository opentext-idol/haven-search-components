/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestTest;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndex;
import org.junit.Before;

import java.io.IOException;
import java.util.Collections;

public class HodGetContentRequestTest extends GetContentRequestTest<HodGetContentRequestIndex> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(GetContentRequestIndex.class, HodGetContentRequestIndexMixin.class);
    }

    @Override
    protected HodGetContentRequest constructObject() {
        return HodGetContentRequestImpl.builder()
                .indexAndReferences(new HodGetContentRequestIndexImpl(ResourceIdentifier.WIKI_ENG, Collections.singleton("Reference1")))
                .print(Print.fields)
                .build();
    }

    @Override
    protected String json() throws IOException {
        return "{\"indexesAndReferences\": [{\"index\": {\"domain\": \"PUBLIC_INDEXES\", \"name\": \"wiki_eng\"}, \"references\": [\"Reference1\"]}], \"print\": \"fields\"}";
    }
}
