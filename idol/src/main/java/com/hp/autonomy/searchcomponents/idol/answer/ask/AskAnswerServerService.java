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

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.opentext.idol.types.responses.answer.AskAnswer;
import com.opentext.idol.types.responses.answer.GetStatusResponsedata;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Service for sending queries to answer server
 */
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
    List<AskAnswer> ask(AskAnswerServerRequest request);

    GetStatusResponsedata getStatus();
}
