/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.test.IntegrationTestUtils;
import com.hp.autonomy.types.requests.Documents;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
public abstract class AbstractDocumentServiceIT<RQ extends QueryRequest<Q>, RS extends SuggestRequest<Q>, RC extends GetContentRequest<?>, Q extends QueryRestrictions<?>, D extends SearchResult, E extends Exception> {
    @Autowired
    protected DocumentsService<RQ, RS, RC, Q, D, E> documentsService;

    @Autowired
    protected IntegrationTestUtils<Q, D, E> integrationTestUtils;

    @Autowired
    protected ObjectFactory<QueryRequestBuilder<RQ, Q, ?>> queryRequestBuilderFactory;

    @Autowired
    protected ObjectFactory<SuggestRequestBuilder<RS, Q, ?>> suggestRequestBuilderFactory;

    @Autowired
    protected ObjectFactory<GetContentRequestBuilder<RC, ?, ?>> getContentRequestBuilder;

    @Test
    public void query() throws E {
        final RQ queryRequest = queryRequestBuilderFactory.getObject()
                .queryRestrictions(integrationTestUtils.buildQueryRestrictions())
                .queryType(QueryRequest.QueryType.MODIFIED)
                .build();
        final Documents<D> documents = documentsService.queryTextIndex(queryRequest);
        assertThat(documents.getDocuments(), is(not(empty())));
    }

    @Test
    public void queryForPromotions() throws E {
        final RQ queryRequest = queryRequestBuilderFactory.getObject()
                .queryRestrictions(integrationTestUtils.buildQueryRestrictions())
                .queryType(QueryRequest.QueryType.PROMOTIONS)
                .build();
        final Documents<D> documents = documentsService.queryTextIndex(queryRequest);
        assertThat(documents.getDocuments(), is(empty())); // TODO: configure this later
    }

    @Test
    public void findSimilar() throws E {
        final String reference = integrationTestUtils.getValidReference();
        final RS suggestRequest = suggestRequestBuilderFactory.getObject()
                .reference(reference)
                .queryRestrictions(integrationTestUtils.buildQueryRestrictions())
                .build();
        final Documents<D> results = documentsService.findSimilar(suggestRequest);
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void getContent() throws E {
        final String reference = integrationTestUtils.getValidReference();
        final List<D> results = documentsService.getDocumentContent(integrationTestUtils.buildGetContentRequest(reference));
        assertThat(results, hasSize(1));
    }
}
