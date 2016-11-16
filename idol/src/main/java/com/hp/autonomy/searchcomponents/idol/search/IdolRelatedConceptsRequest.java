/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import lombok.Builder;
import lombok.Data;

@SuppressWarnings({"FieldMayBeFinal", "WeakerAccess"})
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolRelatedConceptsRequest.IdolRelatedConceptsRequestBuilder.class)
public class IdolRelatedConceptsRequest implements RelatedConceptsRequest<String> {
    private static final long serialVersionUID = 3450911770365743948L;

    private static final int MAX_RESULTS_DEFAULT = 200;

    private final int querySummaryLength;
    private final Integer maxResults;
    private final QueryRestrictions<String> queryRestrictions;

    @SuppressWarnings("unused")
    @JsonPOJOBuilder(withPrefix = "")
    public static class IdolRelatedConceptsRequestBuilder implements RelatedConceptsRequest.RelatedConceptsRequestBuilder<IdolRelatedConceptsRequest, String> {
        private int querySummaryLength = 0;
        private Integer maxResults = MAX_RESULTS_DEFAULT;
    }
}
