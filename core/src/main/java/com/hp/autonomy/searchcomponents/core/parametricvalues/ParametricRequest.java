/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;

import java.io.Serializable;
import java.util.List;

public interface ParametricRequest<S extends Serializable> extends Serializable {
    List<String> getFieldNames();

    Integer getMaxValues();

    SortParam getSort();

    List<Range> getRanges();

    QueryRestrictions<S> getQueryRestrictions();

    boolean isModified();

    interface Builder<P extends ParametricRequest<S>, S extends Serializable> {
        Builder<P, S> setFieldNames(List<String> fieldNames);

        Builder<P, S> setMaxValues(Integer maxValues);

        Builder<P, S> setSort(SortParam sort);

        Builder<P, S> setQueryRestrictions(QueryRestrictions<S> queryRestrictions);

        Builder<P, S> setModified(boolean modified);

        P build();
    }
}
