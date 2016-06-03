/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    public void getFields() throws E {
        final Map<FieldTypeParam, List<TagName>> results = fieldsService.getFields(createFieldsRequest(), FieldTypeParam.Parametric);
        assertThat(results.get(FieldTypeParam.Parametric), is(not(empty())));
    }
}
