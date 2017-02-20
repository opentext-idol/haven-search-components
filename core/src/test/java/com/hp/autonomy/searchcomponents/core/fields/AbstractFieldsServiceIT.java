/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
public abstract class AbstractFieldsServiceIT<R extends FieldsRequest, B extends FieldsRequestBuilder<R, ?>, Q extends QueryRestrictions<?>, E extends Exception> {
    @Autowired
    protected ObjectFactory<B> fieldRequestBuilderFactory;
    @Autowired
    protected TestUtils<Q> testUtils;
    @Autowired
    private FieldsService<R, E> fieldsService;

    protected abstract R createFieldsRequest();

    @Test
    public void getFields() throws E {
        final Map<FieldTypeParam, Set<TagName>> results = fieldsService.getFields(createFieldsRequest());
        assertThat(results.get(FieldTypeParam.Parametric), is(not(empty())));
    }
}
