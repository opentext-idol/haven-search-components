/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.requests.idol.actions.query.QuerySummaryElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
public abstract class AbstractRelatedConceptsServiceIT<R extends RelatedConceptsRequest<Q>, T extends QuerySummaryElement, Q extends QueryRestrictions<?>, E extends Exception> {
    @Autowired
    private RelatedConceptsService<R, T, Q, E> relatedConceptsService;

    @Autowired
    private ObjectFactory<RelatedConceptsRequestBuilder<R, Q, ?>> relatedConceptsRequestBuilderFactory;

    @Autowired
    protected TestUtils<Q> testUtils;

    @Test
    public void findRelatedConcepts() throws E {
        final R request = relatedConceptsRequestBuilderFactory.getObject()
                .queryRestrictions(testUtils.buildQueryRestrictions())
                .querySummaryLength(50)
                .build();
        final List<T> results = relatedConceptsService.findRelatedConcepts(request);
        assertThat(results, is(not(empty())));
    }
}
