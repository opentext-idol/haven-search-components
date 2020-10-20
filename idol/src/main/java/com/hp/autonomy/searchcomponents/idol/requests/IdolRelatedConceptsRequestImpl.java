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
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
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
    private final QueryRequest.QueryType queryType;

    @SuppressWarnings("unused")
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolRelatedConceptsRequestImplBuilder implements IdolRelatedConceptsRequestBuilder {
        private int querySummaryLength = 0;
        private Integer maxResults = MAX_RESULTS_DEFAULT;
        private QueryRequest.QueryType queryType = QueryRequest.QueryType.MODIFIED;
    }
}
