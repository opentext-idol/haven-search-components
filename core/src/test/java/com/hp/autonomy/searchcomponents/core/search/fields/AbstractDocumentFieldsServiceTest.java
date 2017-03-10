/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public abstract class AbstractDocumentFieldsServiceTest {
    @MockBean
    protected ConfigService<HavenSearchCapable> configService;
    @Mock
    protected HavenSearchCapable config;
    protected int numberOfHardCodedFields;
    @Autowired
    private FieldPathNormaliser fieldPathNormaliser;
    @Autowired
    private DocumentFieldsService documentFieldsService;
    private FieldsInfo fieldsInfo;

    @Test
    public void getPrintFieldsHardcodedOnly() {
        fieldsInfo = FieldsInfo.builder().build();
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
        assertThat(documentFieldsService.getPrintFields(Collections.emptyList()), hasSize(numberOfHardCodedFields));
    }

    @Test
    public void getAllPrintFields() {
        fieldsInfo = FieldsInfo.builder()
                .populateResponseMap("Some Id", FieldInfo.<String>builder()
                        .name(fieldPathNormaliser.normaliseFieldPath("SomeField"))
                        .build())
                .populateResponseMap("Some other Id", FieldInfo.<String>builder()
                        .name(fieldPathNormaliser.normaliseFieldPath("SomeOtherField"))
                        .build())
                .build();
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
        assertThat(documentFieldsService.getPrintFields(Collections.emptyList()), hasSize(numberOfHardCodedFields + 2));
    }

    @Test
    public void getAllPrintFieldsWithUserRestrictedFields() {
        fieldsInfo = FieldsInfo.builder()
                .populateResponseMap("Some Id", FieldInfo.<String>builder()
                        .name(fieldPathNormaliser.normaliseFieldPath("SomeField"))
                        .build())
                .populateResponseMap("Some other Id", FieldInfo.<String>builder()
                        .name(fieldPathNormaliser.normaliseFieldPath("SomeOtherField"))
                        .build())
                .build();
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
        assertThat(documentFieldsService.getPrintFields(Collections.singleton("Some Id")), hasSize(1));
    }

}
