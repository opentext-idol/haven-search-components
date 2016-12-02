/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndexTest;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndex;

import java.io.IOException;
import java.util.Collections;

public class IdolGetContentRequestIndexTest extends GetContentRequestIndexTest<String> {
    @Override
    protected IdolGetContentRequestIndex constructObject() {
        return new IdolGetContentRequestIndexImpl("Database1", Collections.singleton("Reference1"));
    }

    @Override
    protected String json() throws IOException {
        return "{\"index\": \"Database1\", \"references\": [\"Reference1\"]}";
    }
}
