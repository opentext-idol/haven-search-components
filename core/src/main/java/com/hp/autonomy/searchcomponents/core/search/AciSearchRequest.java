/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;

import java.io.Serializable;
import java.util.Collection;

public interface AciSearchRequest<S extends Serializable> extends Serializable {
    int DEFAULT_START = 1;
    int DEFAULT_MAX_RESULTS = 30;
    String DEFAULT_PRINT = PrintParam.Fields.name();

    QueryRestrictions<S> getQueryRestrictions();

    int getStart();

    int getMaxResults();

    String getSummary();

    Integer getSummaryCharacters();

    String getSort();

    boolean isHighlight();

    String getPrint();

    Collection<String> getPrintFields();
}
