/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.idol.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.core.parametricvalues.ParametricRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Set;

@Data
@JsonDeserialize(builder = IdolParametricRequest.Builder.class)
public class IdolParametricRequest implements ParametricRequest<String> {
    private static final long serialVersionUID = 3450911770365743948L;

    private final Set<String> databases;
    private final Set<String> fieldNames;
    private final String queryText;
    private final String fieldText;

    private IdolParametricRequest(final Set<String> databases, final Set<String> fieldNames, final String queryText, final String fieldText) {
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
        private Set<String> databases;
        private Set<String> fieldNames;
        private String queryText;
        private String fieldText;

        public Builder(final ParametricRequest<String> parametricRequest) {
            databases = parametricRequest.getDatabases();
            fieldNames = parametricRequest.getFieldNames();
            queryText = parametricRequest.getQueryText();
            fieldText = parametricRequest.getFieldText();
        }

        public IdolParametricRequest build() {
            return new IdolParametricRequest(databases, fieldNames, queryText, fieldText);
        }
    }
}
