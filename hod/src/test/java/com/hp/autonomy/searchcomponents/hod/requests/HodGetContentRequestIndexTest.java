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
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndexTest;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndex;

import java.io.IOException;
import java.util.Collections;

public class HodGetContentRequestIndexTest extends GetContentRequestIndexTest<ResourceName> {
    @Override
    protected HodGetContentRequestIndex constructObject() {
        return new HodGetContentRequestIndexImpl(ResourceName.WIKI_ENG, Collections.singleton("Reference1"));
    }

    @Override
    protected String json() throws IOException {
        return "{\"index\": {\"domain\": \"PUBLIC_INDEXES\", \"name\": \"wiki_eng\"}, \"references\": [\"Reference1\"]}";
    }
}
