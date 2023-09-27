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

package com.hp.autonomy.searchcomponents.core.databases;

import com.hp.autonomy.types.IdolDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@AutoConfigureJson
public abstract class AbstractDatabasesServiceIT<D extends IdolDatabase, R extends DatabasesRequest, B extends DatabasesRequestBuilder<R>, E extends Exception> {
    @Autowired
    private DatabasesService<D, R, E> databasesService;
    @Autowired
    protected ObjectFactory<B> databasesRequestBuilderFactory;

    protected abstract R createDatabasesRequest();

    @Test
    public void getDatabases() throws E {
        final Set<D> results = databasesService.getDatabases(createDatabasesRequest());
        assertThat(results, is(not(empty())));
    }
}
