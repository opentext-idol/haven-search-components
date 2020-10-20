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

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolSuggestRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolSuggestRequestBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collection;

/**
 * Default implementation of {@link IdolSuggestRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolSuggestRequestImpl.IdolSuggestRequestImplBuilder.class)
class IdolSuggestRequestImpl implements IdolSuggestRequest {
    private static final long serialVersionUID = -6338199353489914631L;

    private final String reference;
    private final IdolQueryRestrictions queryRestrictions;
    private final int start;
    private final int maxResults;
    private final String summary;
    private final Integer summaryCharacters;
    private final String sort;
    private final boolean highlight;
    private final boolean intentBasedRanking;
    private final String print;
    @Singular
    private final Collection<String> printFields;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolSuggestRequestImplBuilder implements IdolSuggestRequestBuilder {
        private int start = DEFAULT_START;
        private int maxResults = DEFAULT_MAX_RESULTS;
        private String print = DEFAULT_PRINT.name();
    }
}
