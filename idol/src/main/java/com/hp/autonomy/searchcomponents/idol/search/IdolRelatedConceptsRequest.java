/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@SuppressWarnings({"FieldMayBeFinal", "WeakerAccess"})
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = IdolRelatedConceptsRequest.Builder.class)
public class IdolRelatedConceptsRequest implements RelatedConceptsRequest<String> {
    private static final long serialVersionUID = 3450911770365743948L;

    private static final int MAX_RESULTS_DEFAULT = 200;

    private int querySummaryLength;
    private Integer maxResults;
    private QueryRestrictions<String> queryRestrictions;

    @Component
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder implements RelatedConceptsRequest.Builder<IdolRelatedConceptsRequest, String> {
        private int querySummaryLength = 0;
        private Integer maxResults = MAX_RESULTS_DEFAULT;
        private QueryRestrictions<String> queryRestrictions;
        
        public Builder(final RelatedConceptsRequest<String> relatedConceptsRequest) {
            querySummaryLength = relatedConceptsRequest.getQuerySummaryLength();
            queryRestrictions = relatedConceptsRequest.getQueryRestrictions();
        }

        @Override
        public IdolRelatedConceptsRequest build() {
            return new IdolRelatedConceptsRequest(querySummaryLength, maxResults, queryRestrictions);
        }
    }
}
