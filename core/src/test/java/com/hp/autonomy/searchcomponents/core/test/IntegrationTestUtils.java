/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.test;

import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRequestBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class IntegrationTestUtils<Q extends QueryRestrictions<?>, D extends SearchResult, E extends Exception> {
    @Autowired
    protected DocumentsService<? extends QueryRequest<Q>, ?, ?, Q, D, E> documentsService;

    @Autowired
    protected ObjectFactory<QueryRequestBuilder<? extends QueryRequest<Q>, Q, ?>> queryRequestBuilderFactory;

    @Autowired
    protected TestUtils<Q> testUtils;

    public Q buildQueryRestrictions() {
        return testUtils.buildQueryRestrictions();
    }

    public <RC extends GetContentRequest<?>> RC buildGetContentRequest(final String reference) {
        return testUtils.buildGetContentRequest(reference);
    }

    public String getValidReference() throws E {
        final Q queryRestrictions = buildQueryRestrictions();
        final QueryRequest<Q> queryRequest = queryRequestBuilderFactory.getObject()
                .queryRestrictions(queryRestrictions)
                .build();
        @SuppressWarnings("unchecked")
        final Documents<D> documents = ((DocumentsService<QueryRequest<Q>, ?, ?, Q, D, E>) documentsService).queryTextIndex(queryRequest);
        return documents.getDocuments().get(0).getReference();
    }
}
