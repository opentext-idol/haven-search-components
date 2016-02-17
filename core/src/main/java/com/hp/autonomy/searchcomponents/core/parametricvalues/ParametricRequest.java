/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;

import java.io.Serializable;
import java.util.List;

public interface ParametricRequest<S extends Serializable> extends Serializable {
    List<String> getFieldNames();

    Integer getMaxValues();

    QueryRestrictions<S> getQueryRestrictions();

    boolean isModified();
}
