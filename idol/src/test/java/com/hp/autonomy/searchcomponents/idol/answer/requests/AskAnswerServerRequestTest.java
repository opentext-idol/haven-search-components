/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectTest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequestBuilder;

import java.io.IOException;

public class AskAnswerServerRequestTest extends RequestObjectTest<AskAnswerServerRequest, AskAnswerServerRequestBuilder> {
    @Override
    protected String json() throws IOException {
        return "{\"text\": \"GPU\", \"systemName\": \"answerbank0\", \"maxResults\": 5}";
    }

    @Override
    protected AskAnswerServerRequest constructObject() {
        return AskAnswerServerRequestImpl.builder()
                .text("GPU")
                .systemName("answerbank0")
                .maxResults(5)
                .build();
    }

    @Override
    protected String toStringContent() {
        return "text";
    }
}
