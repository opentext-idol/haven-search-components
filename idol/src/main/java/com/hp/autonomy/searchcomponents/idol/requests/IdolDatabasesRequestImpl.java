/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequest;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequestBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * Default implementation of {@link IdolDatabasesRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolDatabasesRequestImpl.IdolDatabasesRequestImplBuilder.class)
class IdolDatabasesRequestImpl implements IdolDatabasesRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolDatabasesRequestImplBuilder implements IdolDatabasesRequestBuilder {
    }
}
