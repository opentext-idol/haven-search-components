/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.ConversationRequest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.ConversationRequestBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * Default implementation of {@link ConversationRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = ConversationRequestImpl.ConversationRequestImplBuilder.class)
class ConversationRequestImpl implements ConversationRequest {
    private static final long serialVersionUID = -1473588400061840668L;

    private final String text;
    private final String sessionId;

    @JsonPOJOBuilder(withPrefix = "")
    static class ConversationRequestImplBuilder implements ConversationRequestBuilder {
    }
}
