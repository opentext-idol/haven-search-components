/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

@Data
@JsonDeserialize(builder = ParametricRequest.Builder.class)
public class ParametricRequest implements Serializable {
    private static final long serialVersionUID = 2235023046934181036L;

    private final Set<ResourceIdentifier> databases;
    private final Set<String> fieldNames;
    private final String query;
    private final String fieldText;

    private ParametricRequest(final Set<ResourceIdentifier> databases, final Set<String> fieldNames, final String query, final String fieldText) {
        this.databases = databases;
        this.query = query;
        this.fieldText = fieldText;
        this.fieldNames = fieldNames;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private Set<ResourceIdentifier> databases;
        private Set<String> fieldNames;
        private String query;
        private String fieldText;

        public ParametricRequest build() {
             return new ParametricRequest(databases, fieldNames, query, fieldText);
        }
    }
}
