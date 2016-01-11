/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchRequest<S extends Serializable> implements Serializable {
    private static final long serialVersionUID = -6338199353489914631L;

    protected final String queryText;
    protected final String fieldText;
    protected final int start;
    protected final int maxResults;
    protected final String summary;
    protected final List<S> index;
    protected final String languageType;
    protected final String sort;
    protected final DateTime minDate;
    protected final DateTime maxDate;
    protected final boolean highlight;

    protected final QueryType queryType;

    public enum QueryType {
        RAW, MODIFIED, PROMOTIONS
    }
}
