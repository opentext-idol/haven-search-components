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

package com.hp.autonomy.searchcomponents.core.view;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.test.IntegrationTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@ExtendWith(SpringExtension.class)
@AutoConfigureJson
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
