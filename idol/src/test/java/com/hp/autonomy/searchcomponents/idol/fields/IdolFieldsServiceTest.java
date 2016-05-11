/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.google.common.collect.ImmutableList;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.types.idol.GetTagNamesResponseData;
import com.hp.autonomy.types.requests.idol.actions.tags.TagResponse;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolFieldsServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private AciResponseJaxbProcessorFactory processorFactory;

    private IdolFieldsService idolFieldsService;

    @Before
    public void setUp() {
        idolFieldsService = new IdolFieldsService(contentAciService, processorFactory);
    }

    @Test
    public void getParametricFields() {
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(mockTagNamesResponse());
        final List<String> results = idolFieldsService.getParametricFields(new IdolFieldsRequest.Builder().setMaxValues(null).build());
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getFields() {
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(mockTagNamesResponse());
        final TagResponse results = idolFieldsService.getFields(new IdolFieldsRequest.Builder().setMaxValues(null).build(), ImmutableList.of(FieldTypeParam.Date.name(), FieldTypeParam.Numeric.name(), FieldTypeParam.Parametric.name(), FieldTypeParam.Index.name(), FieldTypeParam.Reference.name(), FieldTypeParam.BitField.name()));
        assertNotNull(results.getDateTypeFields());
        assertNotNull(results.getIndexTypeFields());
        assertNotNull(results.getNumericTypeFields());
        assertNotNull(results.getParametricTypeFields());
        assertNotNull(results.getReferenceTypeFields());
    }

    private GetTagNamesResponseData mockTagNamesResponse() {
        final GetTagNamesResponseData responseData = new GetTagNamesResponseData();
        final GetTagNamesResponseData.Name name = new GetTagNamesResponseData.Name();
        name.setValue("DOCUMENT/CATEGORY");
        responseData.getName().add(name);
        return responseData;
    }
}
