/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.test.IntegrationTestUtils;
import com.hp.autonomy.types.requests.idol.actions.query.QuerySummaryElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractRelatedConceptsServiceIT<Q extends QuerySummaryElement, S extends Serializable, D extends SearchResult, E extends Exception> {
    @Autowired
    private RelatedConceptsService<Q, S, E> relatedConceptsService;

    @Autowired
    protected IntegrationTestUtils<S, D, E> integrationTestUtils;

    protected abstract RelatedConceptsRequest<S> createRelatedConceptsRequest();

    @Test
    public void findRelatedConcepts() throws E {
        final List<Q> results = relatedConceptsService.findRelatedConcepts(createRelatedConceptsRequest());
        assertThat(results, is(not(empty())));
    }
}
