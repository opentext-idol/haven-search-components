/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.requests.idol.actions.query.QuerySummaryElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
public abstract class AbstractRelatedConceptsServiceIT<Q extends QuerySummaryElement, S extends Serializable, E extends Exception> {
    @Autowired
    private RelatedConceptsService<Q, S, E> relatedConceptsService;

    @Autowired
    protected TestUtils<S> testUtils;

    protected abstract RelatedConceptsRequest<S> createRelatedConceptsRequest();

    @Test
    public void findRelatedConcepts() throws E {
        final List<Q> results = relatedConceptsService.findRelatedConcepts(createRelatedConceptsRequest());
        assertThat(results, is(not(empty())));
    }
}
