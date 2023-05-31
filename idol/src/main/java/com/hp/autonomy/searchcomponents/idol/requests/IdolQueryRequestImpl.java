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

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolDocumentsService;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collection;

/**
 * Options for interacting with {@link IdolDocumentsService#queryTextIndex(QueryRequest)}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolQueryRequestImpl.IdolQueryRequestImplBuilder.class)
public class IdolQueryRequestImpl implements IdolQueryRequest {
    private static final long serialVersionUID = -6338199353489914631L;

    private final boolean autoCorrect;
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
    private final String referenceField;
    private final QueryRequest.QueryType queryType;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    public static class IdolQueryRequestImplBuilder implements IdolQueryRequestBuilder {
        private int start = DEFAULT_START;
        private int maxResults = DEFAULT_MAX_RESULTS;
        private String print = DEFAULT_PRINT.name();
        private QueryRequest.QueryType queryType = QueryRequest.QueryType.MODIFIED;
        private boolean intentBasedRanking = false;
    }
}
