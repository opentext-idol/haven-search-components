/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequest;
import com.hp.autonomy.searchcomponents.idol.answer.ask.AskAnswerServerRequestBuilder;
import com.hp.autonomy.types.requests.idol.actions.answer.params.AskSortParam;
import java.util.Map;
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
    private final AskSortParam sort;
    @Singular
    private final Set<String> systemNames;
    private final Integer firstResult;
    private final Integer maxResults;
    private final Double minScore;
    private final String customizationData;
    @Singular
    private final Map<String,String> proxiedParams;

    @JsonPOJOBuilder(withPrefix = "")
    static class AskAnswerServerRequestImplBuilder implements AskAnswerServerRequestBuilder {
    }
}
