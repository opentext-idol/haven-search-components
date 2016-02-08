/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = HodFieldsRequest.Builder.class)
public class HodFieldsRequest implements FieldsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private Collection<ResourceIdentifier> databases = Collections.emptySet();
    private Integer maxValues;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private Collection<ResourceIdentifier> databases = Collections.emptySet();
        private Integer maxValues;

        public Builder(final HodFieldsRequest fieldsRequest) {
            databases = fieldsRequest.getDatabases();
            maxValues = fieldsRequest.getMaxValues();
        }

        public HodFieldsRequest build() {
            return new HodFieldsRequest(databases, maxValues);
        }
    }
}
