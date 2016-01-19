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
public class SuggestRequest<S extends Serializable> implements AciSearchRequest<S> {
    private static final long serialVersionUID = -6338199353489914631L;

    protected String reference;
    protected QueryRestrictions<S> queryRestrictions;
    protected int start;
    protected int maxResults;
    protected String summary;
    protected String sort;
    protected boolean highlight;
}
