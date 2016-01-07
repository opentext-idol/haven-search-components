/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonDeserialize(builder = HodParametricRequest.Builder.class)
public class HodParametricRequest implements ParametricRequest<ResourceIdentifier> {
    private static final long serialVersionUID = 2235023046934181036L;

    private Set<ResourceIdentifier> databases;
    private Set<String> fieldNames;
    private String queryText;
    private String fieldText;
    private ResourceIdentifier queryProfile;

    private HodParametricRequest(final ResourceIdentifier queryProfile, final Set<ResourceIdentifier> databases, final Set<String> fieldNames, final String queryText, final String fieldText) {
        this.queryProfile = queryProfile;
        this.databases = databases;
        this.queryText = queryText;
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
        private String queryText;
        private String fieldText;

        public Builder(final HodParametricRequest hodParametricRequest) {
            queryProfile = hodParametricRequest.getQueryProfile();
            databases = hodParametricRequest.getDatabases();
            fieldNames = hodParametricRequest.getFieldNames();
            queryText = hodParametricRequest.getQueryText();
            fieldText = hodParametricRequest.getFieldText();
        }

        public HodParametricRequest build() {
            return new HodParametricRequest(queryProfile, databases, fieldNames, queryText, fieldText);
        }
    }
}
