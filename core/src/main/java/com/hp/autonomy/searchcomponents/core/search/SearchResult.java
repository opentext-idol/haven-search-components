/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import org.joda.time.DateTime;

import java.io.Serializable;

public interface SearchResult extends Serializable {
    String CONTENT_TYPE_FIELD = "content_type";
    String URL_FIELD = "url";
    String OFFSET_FIELD = "offset";
    String DATE_FIELD = "date";

    String getReference();

    String getIndex();

    String getTitle();

    String getSummary();

    Double getWeight();

    String getContentType();

    String getUrl();

    String getOffset();

    DateTime getDate();

    PromotionCategory getPromotionCategory();
}
