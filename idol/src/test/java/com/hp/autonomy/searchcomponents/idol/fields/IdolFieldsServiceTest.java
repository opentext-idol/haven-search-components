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

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.GetTagNamesResponseData;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolFieldsServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private TagNameFactory tagNameFactory;

    @Mock
    private ProcessorFactory processorFactory;

    private IdolFieldsService idolFieldsService;

    @Before
    public void setUp() {
        idolFieldsService = new IdolFieldsServiceImpl(contentAciService, tagNameFactory, processorFactory);
    }

    @Test
    public void getFields() {
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(mockTagNamesResponse());
        assertThat(idolFieldsService.getFields(mock(IdolFieldsRequest.class)), hasEntry(is(FieldTypeParam.Numeric), not(empty())));
    }

    private GetTagNamesResponseData mockTagNamesResponse() {
        final GetTagNamesResponseData responseData = new GetTagNamesResponseData();
        responseData.getName().add(mockNameResponse(null, "DOCUMENT"));
        responseData.getName().add(mockNameResponse("numeric", "DOCUMENT/DREREFERENCE"));
        responseData.getName().add(mockNameResponse("reference,trimspaces", "DOCUMENT/CATEGORY"));
        return responseData;
    }

    private GetTagNamesResponseData.Name mockNameResponse(final String types, final String value) {
        final GetTagNamesResponseData.Name name = new GetTagNamesResponseData.Name();
        name.setTypes(types);
        name.setValue(value);
        return name;
    }
}
