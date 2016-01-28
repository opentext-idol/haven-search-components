/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.test;

import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public abstract class IntegrationTestUtils<S extends Serializable, D extends SearchResult, E extends Exception> {
    @Autowired
    protected DocumentsService<S, D, E> documentsService;

    public abstract List<S> getDatabases();

    public abstract QueryRestrictions<S> buildQueryRestrictions();

    public String getValidReference() throws E {
        final QueryRestrictions<S> queryRestrictions = buildQueryRestrictions();
        final SearchRequest<S> searchRequest = new SearchRequest<>();
        searchRequest.setQueryRestrictions(queryRestrictions);
        final Documents<D> documents = documentsService.queryTextIndex(searchRequest);
        return documents.getDocuments().get(0).getReference();
    }
}
