/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.databases;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import lombok.Builder;
import lombok.Data;

/**
 * Options for interacting with Idol implementation of {@link DatabasesService}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolDatabasesRequest.IdolDatabasesRequestBuilder.class)
public class IdolDatabasesRequest implements DatabasesRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class IdolDatabasesRequestBuilder implements DatabasesRequestBuilder {
    }
}
