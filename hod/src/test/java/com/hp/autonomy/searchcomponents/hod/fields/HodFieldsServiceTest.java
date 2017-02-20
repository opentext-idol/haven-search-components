/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsResponse;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HodFieldsServiceTest {
    @Mock
    private RetrieveIndexFieldsService retrieveIndexFieldsService;
    @Mock
    private RetrieveIndexFieldsResponse response;
    @Mock
    private TagNameFactory tagNameFactory;

    private HodFieldsService fieldsService;

    @Before
    public void setUp() {
        fieldsService = new HodFieldsServiceImpl(retrieveIndexFieldsService, tagNameFactory);
    }

    @Test
    public void getFields() throws HodErrorException {
        when(retrieveIndexFieldsService.retrieveIndexFields(anyListOf(ResourceName.class), any(RetrieveIndexFieldsRequestBuilder.class))).thenReturn(response);
        final Map<FieldTypeParam, Set<TagName>> results = fieldsService.getFields(mock(HodFieldsRequest.class));
        assertThat(results.get(FieldTypeParam.Parametric), is(not(empty())));
    }
}
