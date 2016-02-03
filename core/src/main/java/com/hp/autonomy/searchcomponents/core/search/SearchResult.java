/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public interface SearchResult extends Serializable {
    String CONTENT_TYPE_FIELD = "content_type";
    String URL_FIELD = "url";
    String OFFSET_FIELD = "offset";
    String AUTHOR_FIELD = "author";
    String CATEGORY_FIELD = "category";
    String DATE_FIELD = "date";
    String DATE_CREATED_FIELD = "date_created";
    String CREATED_DATE_FIELD = "created_date";
    String DATE_MODIFIED_FIELD = "date_modified";
    String MODIFIED_DATE_FIELD = "modified_date";

    String getReference();
    String getIndex();
    String getTitle();
    String getSummary();
    Double getWeight();

    String getContentType();
    String getUrl();
    String getOffset();

    List<String> getAuthors();
    List<String> getCategories();

    DateTime getDate();
    DateTime getDateCreated();
    DateTime getDateModified();

    PromotionCategory getPromotionCategory();
}
