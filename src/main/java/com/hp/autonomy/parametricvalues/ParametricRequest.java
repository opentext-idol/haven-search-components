/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Data
@JsonDeserialize(builder = ParametricRequest.Builder.class)
public class ParametricRequest implements Serializable {
    private static final long serialVersionUID = 2235023046934181036L;

    private final ResourceIdentifier queryProfile;
    private final Set<ResourceIdentifier> databases;
    private final Set<String> fieldNames;
    private final String query;
    private final String fieldText;

    private ParametricRequest(final ResourceIdentifier queryProfile, final Set<ResourceIdentifier> databases, final Set<String> fieldNames, final String query, final String fieldText) {
        this.queryProfile = queryProfile;
        this.databases = databases;
        this.query = query;
        this.fieldText = fieldText;
        this.fieldNames = fieldNames == null ? Collections.<String>emptySet() : fieldNames;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private ResourceIdentifier queryProfile;
        private Set<ResourceIdentifier> databases;
        private Set<String> fieldNames;
        private String query;
        private String fieldText;

        public Builder(final ParametricRequest parametricRequest) {
            queryProfile = parametricRequest.getQueryProfile();
            databases = parametricRequest.getDatabases();
            fieldNames = parametricRequest.getFieldNames();
            query = parametricRequest.getQuery();
            fieldText = parametricRequest.getFieldText();
        }

        public ParametricRequest build() {
             return new ParametricRequest(queryProfile, databases, fieldNames, query, fieldText);
        }
    }
}
