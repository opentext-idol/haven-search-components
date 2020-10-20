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
