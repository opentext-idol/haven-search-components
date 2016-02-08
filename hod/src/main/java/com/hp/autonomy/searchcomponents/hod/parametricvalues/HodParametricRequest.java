/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
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

    public static final int MAX_VALUES_DEFAULT = 5;

    private Set<String> fieldNames;
    private Integer maxValues = MAX_VALUES_DEFAULT;
    private QueryRestrictions<ResourceIdentifier> queryRestrictions;

    private HodParametricRequest(final Set<String> fieldNames, final int maxValues, final QueryRestrictions<ResourceIdentifier> queryRestrictions) {
        this.fieldNames = fieldNames == null ? Collections.<String>emptySet() : fieldNames;
        this.maxValues = maxValues;
        this.queryRestrictions = queryRestrictions;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private Set<String> fieldNames;
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private QueryRestrictions<ResourceIdentifier> queryRestrictions;

        public Builder(final ParametricRequest<ResourceIdentifier> hodParametricRequest) {
            fieldNames = hodParametricRequest.getFieldNames();
            maxValues = hodParametricRequest.getMaxValues();
            queryRestrictions = hodParametricRequest.getQueryRestrictions();
        }

        public HodParametricRequest build() {
            return new HodParametricRequest(fieldNames, maxValues, queryRestrictions);
        }
    }
}
