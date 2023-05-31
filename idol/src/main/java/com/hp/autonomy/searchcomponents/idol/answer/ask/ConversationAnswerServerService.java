/*
 * Copyright 2018 Open Text.
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

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.types.idol.responses.conversation.ConversePrompt;
import java.util.List;
import java.util.Map;
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
     * @param sessionAttributes optional map of session attributes to pre-initialize in the conversation.
     * @return the session ID if available, or null if we couldn't create one
     */
    String conversationStart(Map<String, String> sessionAttributes);

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
