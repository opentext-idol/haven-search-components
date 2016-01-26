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

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = IdolRelatedConceptsRequest.Builder.class)
public class IdolRelatedConceptsRequest implements RelatedConceptsRequest<String> {
    private static final long serialVersionUID = 3450911770365743948L;

    private int querySummaryLength;
    private QueryRestrictions<String> queryRestrictions;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private int maxRelatedConcepts = 0;
        private QueryRestrictions<String> queryRestrictions;
        
        public Builder(final RelatedConceptsRequest<String> relatedConceptsRequest) {
            maxRelatedConcepts = relatedConceptsRequest.getQuerySummaryLength();
            queryRestrictions = relatedConceptsRequest.getQueryRestrictions();
        }

        public IdolRelatedConceptsRequest build() {
            return new IdolRelatedConceptsRequest(maxRelatedConcepts, queryRestrictions);
        }
    }
}
