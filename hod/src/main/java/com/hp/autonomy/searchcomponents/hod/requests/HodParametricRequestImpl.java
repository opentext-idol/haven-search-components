/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.aci.content.ranges.ParametricFieldRange;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequest;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Options for interacting with HoD implementation of {@link ParametricValuesService}
 */
@SuppressWarnings("WeakerAccess")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodParametricRequestImpl.HodParametricRequestImplBuilder.class)
class HodParametricRequestImpl implements HodParametricRequest {
    public static final int MAX_VALUES_DEFAULT = 5;
    private static final long serialVersionUID = -7181783205453758678L;

    @Singular
    private final List<FieldPath> fieldNames;
    private final Integer start;
    private final Integer maxValues;
    private final SortParam sort;
    @Singular
    private final List<ParametricFieldRange> ranges;
    @Singular
    private final List<String> valueRestrictions;
    private final HodQueryRestrictions queryRestrictions;
    private final boolean modified;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    static class HodParametricRequestImplBuilder implements HodParametricRequestBuilder {
        private Integer maxValues = MAX_VALUES_DEFAULT;
        private Integer start = START_DEFAULT;
        private SortParam sort = SortParam.DocumentCount;
        private boolean modified = true;
    }
}
