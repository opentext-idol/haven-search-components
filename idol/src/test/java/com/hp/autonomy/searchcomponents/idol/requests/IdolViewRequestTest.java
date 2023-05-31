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

import com.hp.autonomy.searchcomponents.core.view.ViewRequest;
import com.hp.autonomy.searchcomponents.core.view.ViewRequestTest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class IdolViewRequestTest extends ViewRequestTest<String> {
    @Override
    protected ViewRequest<String> constructObject() {
        return IdolViewRequestImpl.builder()
                .documentReference("dede952d-8a4d-4f54-ac1f-5187bf10a744")
                .database("SomeDatabase")
                .highlightExpression("SomeExpression")
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/view/viewRequest.json"));
    }
}
