/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface ParametricValuesService<R extends ParametricRequest<S>, S extends Serializable, E extends Exception> {

    String AUTN_DATE_FIELD = "AUTN_DATE";

    Set<QueryTagInfo> getAllParametricValues(R parametricRequest) throws E;

    Set<QueryTagInfo> getNumericParametricValues(R parametricRequest) throws E;

    Set<QueryTagInfo> getDateParametricValues(R parametricRequest) throws E;

    List<RecursiveField> getDependentParametricValues(R parametricRequest) throws E;

}
