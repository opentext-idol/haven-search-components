/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
                .referenceField("ref")
                .build();
    }

    @Override
    protected String json() throws IOException {
        return "" +
            "{" +
            "    \"indexesAndReferences\": [{\"index\": \"Database1\", \"references\": [\"Reference1\"]}]," +
            "    \"referenceField\": \"ref\"" +
            "}";
    }
}
