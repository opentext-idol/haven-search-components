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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@SuppressWarnings({"FieldMayBeFinal", "WeakerAccess"})
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = HodRelatedConceptsRequest.Builder.class)
public class HodRelatedConceptsRequest implements RelatedConceptsRequest<ResourceIdentifier> {
    private static final long serialVersionUID = 3450911770365743948L;

    private static final int MAX_RESULTS_DEFAULT = 200;

    private int querySummaryLength;
    private Integer maxResults;
    private QueryRestrictions<ResourceIdentifier> queryRestrictions;

    @Component
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder implements RelatedConceptsRequest.Builder<HodRelatedConceptsRequest, ResourceIdentifier> {
        private int querySummaryLength = 0;
        private Integer maxResults = MAX_RESULTS_DEFAULT;
        private QueryRestrictions<ResourceIdentifier> queryRestrictions;

        public Builder(final RelatedConceptsRequest<ResourceIdentifier> relatedConceptsRequest) {
            querySummaryLength = relatedConceptsRequest.getQuerySummaryLength();
            queryRestrictions = relatedConceptsRequest.getQueryRestrictions();
        }

        @Override
        public HodRelatedConceptsRequest build() {
            return new HodRelatedConceptsRequest(querySummaryLength, maxResults, queryRestrictions);
        }
    }
}
