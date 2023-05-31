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
