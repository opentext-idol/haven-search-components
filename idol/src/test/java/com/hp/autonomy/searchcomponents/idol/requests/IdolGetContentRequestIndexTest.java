/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
