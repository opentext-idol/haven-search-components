/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.searchcomponents.idol.parametricvalues.IdolParametricRequest;
import com.hp.autonomy.searchcomponents.idol.parametricvalues.IdolParametricRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Default implementation of {@link IdolParametricRequest}
 */
@SuppressWarnings("WeakerAccess")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolParametricRequestImpl.IdolParametricRequestImplBuilder.class)
class IdolParametricRequestImpl implements IdolParametricRequest {
    private static final int MAX_VALUES_DEFAULT = 10;

    private static final long serialVersionUID = 3450911770365743948L;

    @Singular
    private final List<TagName> fieldNames;
    private final Integer start;
    private final Integer maxValues;
    private final SortParam sort;
    @Singular
    private final List<Range> ranges;
    private final IdolQueryRestrictions queryRestrictions;
    private final boolean modified;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolParametricRequestImplBuilder implements IdolParametricRequestBuilder {
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private Integer start = START_DEFAULT;
        private SortParam sort = SortParam.DocumentCount;
        private boolean modified = true;
    }
}
