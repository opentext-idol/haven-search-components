/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.view.ViewRequest;
import com.hp.autonomy.searchcomponents.core.view.ViewRequestTest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class HodViewRequestTest extends ViewRequestTest<ResourceName> {
    @Override
    protected ViewRequest<ResourceName> constructObject() {
        return HodViewRequestImpl.<ResourceName>builder()
                .documentReference("dede952d-8a4d-4f54-ac1f-5187bf10a744")
                .database(ResourceName.WIKI_ENG)
                .highlightExpression("SomeExpression")
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/hod/view/viewRequest.json"));
    }
}
