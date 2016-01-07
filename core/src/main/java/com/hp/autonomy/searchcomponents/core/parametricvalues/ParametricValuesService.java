/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;

import java.io.Serializable;
import java.util.Set;

public interface ParametricValuesService<R extends ParametricRequest<S>, S extends Serializable, E extends Exception> {

    Set<QueryTagInfo> getAllParametricValues(R parametricRequest) throws E;

}
