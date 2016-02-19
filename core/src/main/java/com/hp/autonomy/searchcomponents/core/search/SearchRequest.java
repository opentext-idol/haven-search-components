/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest<S extends Serializable> implements AciSearchRequest<S> {
    private static final long serialVersionUID = -6338199353489914631L;

    protected String securityInfo;
    protected QueryRestrictions<S> queryRestrictions;
    protected int start = DEFAULT_START;
    protected int maxResults = DEFAULT_MAX_RESULTS;
    protected String summary;
    protected Integer summaryCharacters;
    protected String sort;
    protected boolean highlight;
    protected boolean autoCorrect;

    protected QueryType queryType;

    public enum QueryType {
        RAW, MODIFIED, PROMOTIONS
    }
}
