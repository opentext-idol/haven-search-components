/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import lombok.Builder;
import lombok.Data;

/**
 * Options for interacting with HoD implementation of {@link DatabasesService}
 */
@SuppressWarnings("WeakerAccess")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodDatabasesRequest.HodDatabasesRequestBuilder.class)
public class HodDatabasesRequest implements DatabasesRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private final boolean publicIndexesEnabled;

    @JsonPOJOBuilder(withPrefix = "")
    public static class HodDatabasesRequestBuilder implements DatabasesRequest.DatabasesRequestBuilder {
    }
}
