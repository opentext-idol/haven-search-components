/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

/**
 * Builder for {@link ConversationRequest}
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public interface ConversationRequestBuilder extends RequestObjectBuilder<ConversationRequest, ConversationRequestBuilder> {
    /**
     * Sets the query text
     *
     * @param text The query text
     * @return the builder (for chaining)
     */
    ConversationRequestBuilder text(String text);

    /**
     * Sets the session id
     *
     * @param sessionId The session id
     * @return the builder (for chaining)
     */
    ConversationRequestBuilder sessionId(String sessionId);

}
