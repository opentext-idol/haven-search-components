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

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
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
                .indexAndReferences(new HodGetContentRequestIndexImpl(ResourceName.WIKI_ENG, Collections.singleton("Reference1")))
                .print(Print.fields)
                .build();
    }

    @Override
    protected String json() throws IOException {
        return "{\"indexesAndReferences\": [{\"index\": {\"domain\": \"PUBLIC_INDEXES\", \"name\": \"wiki_eng\"}, \"references\": [\"Reference1\"]}], \"print\": \"fields\"}";
    }
}
