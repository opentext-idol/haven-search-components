/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.test;

import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public abstract class IntegrationTestUtils<S extends Serializable, D extends SearchResult, E extends Exception> {
    @Autowired
    protected DocumentsService<S, D, E> documentsService;

    @Autowired
    protected TestUtils<S> testUtils;

    public List<S> getDatabases() {
        return testUtils.getDatabases();
    }

    public QueryRestrictions<S> buildQueryRestrictions() {
        return testUtils.buildQueryRestrictions();
    }

    public String getValidReference() throws E {
        final QueryRestrictions<S> queryRestrictions = buildQueryRestrictions();
        final QueryRequest<S> queryRequest = QueryRequest.<S>builder()
                .queryRestrictions(queryRestrictions)
                .build();
        final Documents<D> documents = documentsService.queryTextIndex(queryRequest);
        return documents.getDocuments().get(0).getReference();
    }
}
