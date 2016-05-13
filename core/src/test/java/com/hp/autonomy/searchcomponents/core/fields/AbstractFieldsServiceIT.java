/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.google.common.collect.ImmutableList;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.requests.idol.actions.tags.TagResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractFieldsServiceIT<R extends FieldsRequest, S extends Serializable, E extends Exception> {
    @Autowired
    private FieldsService<R, E> fieldsService;

    @Autowired
    protected TestUtils<S> testUtils;

    protected abstract R createFieldsRequest();

    @Test
    public void getParametricFields() throws E {
        final List<String> results = fieldsService.getParametricFields(createFieldsRequest());
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getFields() throws E {
        final TagResponse results = fieldsService.getFields(createFieldsRequest(), ImmutableList.of("parametric"));
        assertThat(results.getParametricTypeFields(), is(not(empty())));
    }
}
