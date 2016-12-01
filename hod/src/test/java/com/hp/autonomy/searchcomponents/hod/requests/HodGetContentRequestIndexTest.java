/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndexTest;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndex;

import java.io.IOException;
import java.util.Collections;

public class HodGetContentRequestIndexTest extends GetContentRequestIndexTest<ResourceIdentifier> {
    @Override
    protected HodGetContentRequestIndex constructObject() {
        return new HodGetContentRequestIndexImpl(ResourceIdentifier.WIKI_ENG, Collections.singleton("Reference1"));
    }

    @Override
    protected String json() throws IOException {
        return "{\"index\": {\"domain\": \"PUBLIC_INDEXES\", \"name\": \"wiki_eng\"}, \"references\": [\"Reference1\"]}";
    }
}
