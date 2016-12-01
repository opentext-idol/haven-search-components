/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.types.idol.responses.answer.Answer;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service for sending queries to answer server
 */
@FunctionalInterface
public interface AskAnswerServerService {
    /**
     * The bean name of the default ask request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String ASK_SERVICE_BEAN_NAME = "askService";

    /**
     * The bean name of the default ask request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String ASK_REQUEST_BUILDER_BEAN_NAME = "askRequestBuilder";

    /**
     * Query AnswerServer
     *
     * @param request query options
     * @return answers returned by AnswerServer (may be empty)
     */
    List<Answer> ask(AskAnswerServerRequest request);
}
