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

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
public abstract class AbstractRelatedConceptsServiceIT<Q extends QuerySummaryElement, S extends Serializable, E extends Exception> {
    @Autowired
    private RelatedConceptsService<Q, S, E> relatedConceptsService;

    @Autowired
    private RelatedConceptsRequest.RelatedConceptsRequestBuilder<? extends RelatedConceptsRequest<S>, S> relatedConceptsRequestBuilder;

    @Autowired
    protected TestUtils<S> testUtils;

    @Test
    public void findRelatedConcepts() throws E {
        final RelatedConceptsRequest<S> request = relatedConceptsRequestBuilder
                .queryRestrictions(testUtils.buildQueryRestrictions())
                .querySummaryLength(50)
                .build();
        final List<Q> results = relatedConceptsService.findRelatedConcepts(request);
        assertThat(results, is(not(empty())));
    }
}
