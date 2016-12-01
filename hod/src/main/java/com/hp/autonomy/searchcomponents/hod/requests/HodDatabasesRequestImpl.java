/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesRequest;
import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesRequestBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * Default implementation of {@link HodDatabasesRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodDatabasesRequestImpl.HodDatabasesRequestImplBuilder.class)
class HodDatabasesRequestImpl implements HodDatabasesRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private final boolean publicIndexesEnabled;

    @JsonPOJOBuilder(withPrefix = "")
    static class HodDatabasesRequestImplBuilder implements HodDatabasesRequestBuilder {
    }
}
