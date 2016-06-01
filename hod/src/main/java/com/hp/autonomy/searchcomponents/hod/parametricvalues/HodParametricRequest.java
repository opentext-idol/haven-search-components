/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Data
@JsonDeserialize(builder = HodParametricRequest.Builder.class)
public class HodParametricRequest implements ParametricRequest<ResourceIdentifier> {
    private static final long serialVersionUID = 2235023046934181036L;

    public static final int MAX_VALUES_DEFAULT = 5;

    private final List<String> fieldNames;
    private final Integer maxValues;
    private final SortParam sort;
    private final List<Range> ranges;
    private final QueryRestrictions<ResourceIdentifier> queryRestrictions;
    private final boolean modified;

    private HodParametricRequest(final Builder builder) {
        fieldNames = builder.fieldNames;
        maxValues = builder.maxValues;
        sort = builder.sort;
        ranges = builder.ranges;
        queryRestrictions = builder.queryRestrictions;
        modified = builder.modified;
    }

    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder implements ParametricRequest.Builder<HodParametricRequest, ResourceIdentifier> {
        private List<String> fieldNames = Collections.emptyList();
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private SortParam sort = SortParam.DocumentCount;
        private List<Range> ranges;
        private QueryRestrictions<ResourceIdentifier> queryRestrictions;
        private boolean modified = true;

        public Builder(final ParametricRequest<ResourceIdentifier> hodParametricRequest) {
            fieldNames = hodParametricRequest.getFieldNames();
            maxValues = hodParametricRequest.getMaxValues();
            sort = hodParametricRequest.getSort();
            ranges = hodParametricRequest.getRanges();
            queryRestrictions = hodParametricRequest.getQueryRestrictions();
            modified = hodParametricRequest.isModified();
        }

        @Override
        public HodParametricRequest build() {
            return new HodParametricRequest(this);
        }
    }
}
