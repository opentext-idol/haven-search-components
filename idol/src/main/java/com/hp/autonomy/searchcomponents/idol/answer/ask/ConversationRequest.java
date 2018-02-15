/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskSortParam;
import java.util.Map;
import java.util.Set;

/**
 * Options for interacting with {@link ConversationAnswerServerService}
 */
@SuppressWarnings("WeakerAccess")
public interface ConversationRequest extends RequestObject<ConversationRequest, ConversationRequestBuilder> {
    /**
     * The query text
     *
     * @return The query text
     */
    String getText();

    /**
     * The conversation session id
     *
     * @return The session ID
     */
    String getSessionId();

}
