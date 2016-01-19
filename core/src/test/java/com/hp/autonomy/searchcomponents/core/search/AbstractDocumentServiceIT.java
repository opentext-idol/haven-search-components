/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.Documents;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractDocumentServiceIT<S extends Serializable, D extends SearchResult, E extends Exception> {
    @Autowired
    protected DocumentsService<S, D, E> documentsService;

    protected final List<S> indexes;

    protected AbstractDocumentServiceIT(final List<S> indexes) {
        this.indexes = new ArrayList<>(indexes);
    }

    protected abstract QueryRestrictions<S> buildQueryRestrictions();

    @Test
    public void query() throws E {
        final SearchRequest<S> searchRequest = new SearchRequest<>();
        searchRequest.setQueryRestrictions(buildQueryRestrictions());
        final Documents<D> documents = documentsService.queryTextIndex(searchRequest);
        assertThat(documents.getDocuments(), is(not(empty())));
    }

    @Test
    public void queryForPromotions() throws E {
        final SearchRequest<S> searchRequest = new SearchRequest<>();
        searchRequest.setQueryRestrictions(buildQueryRestrictions());
        final Documents<D> documents = documentsService.queryTextIndexForPromotions(searchRequest);
        assertThat(documents.getDocuments(), is(empty())); // TODO: configure this later
    }

    @Test
    public void findSimilar() throws E {
        final SearchRequest<S> searchRequest = new SearchRequest<>();
        final QueryRestrictions<S> queryRestrictions = buildQueryRestrictions();
        searchRequest.setQueryRestrictions(queryRestrictions);
        final Documents<D> documents = documentsService.queryTextIndex(searchRequest);

        final SuggestRequest<S> suggestRequest = new SuggestRequest<>();
        suggestRequest.setReference(documents.getDocuments().get(0).getReference());
        suggestRequest.setQueryRestrictions(queryRestrictions);
        final Documents<D> results = documentsService.findSimilar(suggestRequest);
        assertThat(results.getDocuments(), is(not(empty())));
    }
}
