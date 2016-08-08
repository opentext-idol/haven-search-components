/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import org.joda.time.DateTime;

import java.io.Serializable;

public interface SearchResult extends Serializable {
    String getReference();

    String getIndex();

    String getTitle();

    String getSummary();

    Double getWeight();

    DateTime getDate();

    PromotionCategory getPromotionCategory();
}
