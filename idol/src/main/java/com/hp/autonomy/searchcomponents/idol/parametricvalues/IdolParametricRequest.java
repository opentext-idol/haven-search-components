/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
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

@SuppressWarnings("WeakerAccess")
@Data
@JsonDeserialize(builder = IdolParametricRequest.Builder.class)
public class IdolParametricRequest implements ParametricRequest<String> {
    private static final int MAX_VALUES_DEFAULT = 10;

    private static final long serialVersionUID = 3450911770365743948L;

    private final List<String> fieldNames;
    private final Integer maxValues;
    private final String sort;
    private final QueryRestrictions<String> queryRestrictions;
    private final boolean modified;

    private IdolParametricRequest(final Builder builder) {
        fieldNames = builder.fieldNames;
        maxValues = builder.maxValues;
        sort = builder.sort;
        queryRestrictions = builder.queryRestrictions;
        modified = builder.modified;
    }

    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder implements ParametricRequest.Builder<IdolParametricRequest, String> {
        private List<String> fieldNames = Collections.emptyList();
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private String sort = SortParam.DocumentCount.name();
        private QueryRestrictions<String> queryRestrictions;
        private boolean modified = true;

        public Builder(final ParametricRequest<String> parametricRequest) {
            fieldNames = parametricRequest.getFieldNames();
            maxValues = parametricRequest.getMaxValues();
            sort = parametricRequest.getSort();
            queryRestrictions = parametricRequest.getQueryRestrictions();
            modified = parametricRequest.isModified();
        }

        @Override
        public IdolParametricRequest build() {
            return new IdolParametricRequest(this);
        }
    }
}
