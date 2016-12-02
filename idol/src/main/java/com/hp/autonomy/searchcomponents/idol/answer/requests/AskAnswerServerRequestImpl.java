/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequestBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * Default implementation of {@link AskAnswerServerRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = AskAnswerServerRequestImpl.AskAnswerServerRequestImplBuilder.class)
class AskAnswerServerRequestImpl implements AskAnswerServerRequest {
    private static final long serialVersionUID = -1473588400061840668L;

    private final String text;
    @Singular
    private final Set<String> systemNames;
    private final Integer maxResults;

    @JsonPOJOBuilder(withPrefix = "")
    static class AskAnswerServerRequestImplBuilder implements AskAnswerServerRequestBuilder {
    }
}
