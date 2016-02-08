/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = IdolParametricRequest.Builder.class)
public class IdolParametricRequest implements ParametricRequest<String> {
    private static final int MAX_VALUES_DEFAULT = 10;

    private static final long serialVersionUID = 3450911770365743948L;

    private Set<String> fieldNames = Collections.emptySet();
    private Integer maxValues = MAX_VALUES_DEFAULT;
    private QueryRestrictions<String> queryRestrictions;
    private boolean modified = true;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private Set<String> fieldNames = Collections.emptySet();
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private QueryRestrictions<String> queryRestrictions;
        private boolean modified = true;
        
        public Builder(final ParametricRequest<String> parametricRequest) {
            fieldNames = parametricRequest.getFieldNames();
            maxValues = parametricRequest.getMaxValues();
            queryRestrictions = parametricRequest.getQueryRestrictions();
            modified = parametricRequest.isModified();
        }

        public IdolParametricRequest build() {
            return new IdolParametricRequest(fieldNames, maxValues, queryRestrictions, modified);
        }
    }
}
