/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.types.idol.responses.conversation.ConversePrompt;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Service for sending queries to answer server
 */
public interface ConversationAnswerServerService {
    /**
     * The bean name of the default conversation request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String CONVERSATION_SERVICE_BEAN_NAME = "conversationService";

    /**
     * The bean name of the default conversation request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String CONVERSATION_REQUEST_BUILDER_BEAN_NAME = "conversationRequestBuilder";

    /**
     * Starts a new conversation
     * @return the session ID if available, or null if we couldn't create one
     */
    String conversationStart();

    /**
     * Terminates sessions
     * @param sessionIds the sessions to terminate
     */
    void conversationEnd(String... sessionIds);

    /**
     * Query AnswerServer's conversation module
     *
     * @param request query options
     * @return answers returned by AnswerServer (may be empty)
     */
    List<ConversePrompt> converse(ConversationRequest request);
}
