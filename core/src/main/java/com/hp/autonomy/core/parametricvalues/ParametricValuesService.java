/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.core.parametricvalues;

import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface ParametricValuesService<R extends ParametricRequest, T extends QueryTagInfo<C>, C extends QueryTagCountInfo, E extends Exception> {

    Set<T> getAllParametricValues(R parametricRequest) throws E;

}
