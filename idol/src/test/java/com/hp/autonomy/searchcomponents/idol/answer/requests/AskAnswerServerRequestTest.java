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

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequestBuilder;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskSortParam;

import java.io.IOException;

public class AskAnswerServerRequestTest extends RequestObjectTest<AskAnswerServerRequest, AskAnswerServerRequestBuilder> {
    @Override
    protected String json() throws IOException {
        return "{\"text\": \"GPU\", \"sort\": \"SYSTEM\", \"systemName\": \"answerbank0\", \"maxResults\": 5, \"minScore\": 0}";
    }

    @Override
    protected AskAnswerServerRequest constructObject() {
        return AskAnswerServerRequestImpl.builder()
                .text("GPU")
                .sort(AskSortParam.SYSTEM)
                .systemName("answerbank0")
                .maxResults(5)
                .minScore(0d)
                .build();
    }

    @Override
    protected String toStringContent() {
        return "text";
    }
}
