/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.databases;

import com.hp.autonomy.types.IdolDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
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
