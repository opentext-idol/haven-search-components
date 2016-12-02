/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodRelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodRelatedConceptsRequestBuilder;
import lombok.Builder;
import lombok.Data;

@SuppressWarnings({"FieldMayBeFinal", "WeakerAccess"})
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodRelatedConceptsRequestImpl.HodRelatedConceptsRequestImplBuilder.class)
class HodRelatedConceptsRequestImpl implements HodRelatedConceptsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private static final int MAX_RESULTS_DEFAULT = 200;

    private final int querySummaryLength;
    private final Integer maxResults;
    private final HodQueryRestrictions queryRestrictions;

    @SuppressWarnings("unused")
    @JsonPOJOBuilder(withPrefix = "")
    static class HodRelatedConceptsRequestImplBuilder implements HodRelatedConceptsRequestBuilder {
        private int querySummaryLength = 0;
        private Integer maxResults = MAX_RESULTS_DEFAULT;
    }
}
