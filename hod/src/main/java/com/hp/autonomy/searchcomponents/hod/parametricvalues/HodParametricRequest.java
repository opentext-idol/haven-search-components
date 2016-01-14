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

    private Set<String> fieldNames;
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private QueryRestrictions<ResourceIdentifier> queryRestrictions;
    private ResourceIdentifier queryProfile;

    private HodParametricRequest(final Set<String> fieldNames, final QueryRestrictions<ResourceIdentifier> queryRestrictions, final ResourceIdentifier queryProfile) {
        this.fieldNames = fieldNames == null ? Collections.<String>emptySet() : fieldNames;
        this.queryRestrictions = queryRestrictions;
        this.queryProfile = queryProfile;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private Set<String> fieldNames;
        private ResourceIdentifier queryProfile;
        @SuppressWarnings("InstanceVariableOfConcreteClass")
        private QueryRestrictions<ResourceIdentifier> queryRestrictions;

        public Builder(final HodParametricRequest hodParametricRequest) {
            fieldNames = hodParametricRequest.getFieldNames();
            queryRestrictions = hodParametricRequest.getQueryRestrictions();
            queryProfile = hodParametricRequest.getQueryProfile();
        }

        public HodParametricRequest build() {
            return new HodParametricRequest(fieldNames, queryRestrictions, queryProfile);
        }
    }
}
