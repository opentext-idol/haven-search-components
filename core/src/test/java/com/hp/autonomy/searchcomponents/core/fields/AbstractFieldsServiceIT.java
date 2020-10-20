/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
