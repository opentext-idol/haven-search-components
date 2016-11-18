/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Options for interacting with Idol implementation of {@link ParametricValuesService}
 */
@SuppressWarnings("WeakerAccess")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolParametricRequest.IdolParametricRequestBuilder.class)
public class IdolParametricRequest implements ParametricRequest<String> {
    private static final int MAX_VALUES_DEFAULT = 10;

    private static final long serialVersionUID = 3450911770365743948L;

    @Singular
    private final List<String> fieldNames;
    private final Integer maxValues;
    private final SortParam sort;
    @Singular
    private final List<Range> ranges;
    private final QueryRestrictions<String> queryRestrictions;
    private final boolean modified;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    public static class IdolParametricRequestBuilder implements ParametricRequest.ParametricRequestBuilder<IdolParametricRequest, String> {
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private SortParam sort = SortParam.DocumentCount;
        private boolean modified = true;
    }
}
