/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import lombok.*;
import lombok.experimental.Accessors;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = HodRelatedConceptsRequest.Builder.class)
public class HodRelatedConceptsRequest implements RelatedConceptsRequest<ResourceIdentifier> {
    private static final long serialVersionUID = 3450911770365743948L;

    private int querySummaryLength;
    private QueryRestrictions<ResourceIdentifier> queryRestrictions;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private int querySummaryLength = 0;
        private QueryRestrictions<ResourceIdentifier> queryRestrictions;
        
        public Builder(final RelatedConceptsRequest<ResourceIdentifier> relatedConceptsRequest) {
            querySummaryLength = relatedConceptsRequest.getQuerySummaryLength();
            queryRestrictions = relatedConceptsRequest.getQueryRestrictions();
        }

        public HodRelatedConceptsRequest build() {
            return new HodRelatedConceptsRequest(querySummaryLength, queryRestrictions);
        }
    }
}
