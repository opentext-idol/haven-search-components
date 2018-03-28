/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodDocumentsService;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collection;
import java.util.List;

/**
 * Options for interacting with {@link HodDocumentsService#queryTextIndex(QueryRequest)}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodQueryRequestImpl.HodQueryRequestImplBuilder.class)
class HodQueryRequestImpl implements HodQueryRequest {
    private static final long serialVersionUID = -6338199353489914631L;

    private final boolean autoCorrect;
    private final HodQueryRestrictions queryRestrictions;
    private final int start;
    private final int maxResults;
    private final String summary;
    private final Integer summaryCharacters;
    private final String sort;
    private final boolean highlight;
    private final String print;
    @Singular
    private final Collection<String> printFields;
    private final QueryType queryType;

    // State tokens not yet supported in HoD
    @Singular
    private final List<String> stateMatchIds;
    @Singular
    private final List<String> stateDontMatchIds;

    private final boolean intentBasedRanking;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    static class HodQueryRequestImplBuilder implements HodQueryRequestBuilder {
        private int start = DEFAULT_START;
        private int maxResults = DEFAULT_MAX_RESULTS;
        private String print = DEFAULT_PRINT.name();
        private QueryType queryType = QueryType.MODIFIED;
        private boolean intentBasedRanking = false;
    }
}
