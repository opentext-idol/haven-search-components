/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolRelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolRelatedConceptsRequestBuilder;
import lombok.Builder;
import lombok.Data;

@SuppressWarnings({"FieldMayBeFinal", "WeakerAccess"})
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolRelatedConceptsRequestImpl.IdolRelatedConceptsRequestImplBuilder.class)
class IdolRelatedConceptsRequestImpl implements IdolRelatedConceptsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private static final int MAX_RESULTS_DEFAULT = 200;

    private final int querySummaryLength;
    private final Integer maxResults;
    private final IdolQueryRestrictions queryRestrictions;

    @SuppressWarnings("unused")
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolRelatedConceptsRequestImplBuilder implements IdolRelatedConceptsRequestBuilder {
        private int querySummaryLength = 0;
        private Integer maxResults = MAX_RESULTS_DEFAULT;
    }
}
