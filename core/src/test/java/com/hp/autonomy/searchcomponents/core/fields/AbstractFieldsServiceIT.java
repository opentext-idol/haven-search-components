/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.test.TestUtils;
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
}
