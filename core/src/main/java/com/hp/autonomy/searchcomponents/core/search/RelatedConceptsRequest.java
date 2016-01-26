/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import java.io.Serializable;
import java.util.Set;

public interface RelatedConceptsRequest<S extends Serializable> extends Serializable {
    int getQuerySummaryLength();

    QueryRestrictions<S> getQueryRestrictions();
}
