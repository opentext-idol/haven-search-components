/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = HodDatabasesRequest.Builder.class)
public class HodDatabasesRequest implements DatabasesRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private boolean publicIndexesEnabled;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private boolean publicIndexesEnabled;

        public Builder(final HodDatabasesRequest databasesRequest) {
            publicIndexesEnabled = databasesRequest.isPublicIndexesEnabled();
        }

        public HodDatabasesRequest build() {
            return new HodDatabasesRequest(publicIndexesEnabled);
        }
    }
}
