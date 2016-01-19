/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import java.io.Serializable;

public interface AciSearchRequest<S extends Serializable> extends Serializable {
    QueryRestrictions<S> getQueryRestrictions();

    int getStart();

    int getMaxResults();

    String getSummary();

    String getSort();

    boolean isHighlight();
}
