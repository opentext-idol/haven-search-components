/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.test.IntegrationTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.assertNotNull;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
public abstract class AbstractViewServerServiceIT<R extends ViewRequest<S>, Q extends QueryRestrictions<S>, S extends Serializable, D extends SearchResult, E extends Exception> {
    @Autowired
    protected ViewServerService<R, S, E> viewServerService;

    @Autowired
    protected IntegrationTestUtils<Q, D, E> integrationTestUtils;

    @Autowired
    protected ObjectFactory<ViewRequestBuilder<R, S, ?>> viewRequestBuilderFactory;

    @Test
    public void viewDocument() throws E, IOException {
        final String reference = integrationTestUtils.getValidReference();

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final R request = viewRequestBuilderFactory.getObject()
                .documentReference(reference)
                .database(integrationTestUtils.buildQueryRestrictions().getDatabases().get(0))
                .build();
        viewServerService.viewDocument(request, outputStream);
        assertNotNull(outputStream.toByteArray());
    }
}
