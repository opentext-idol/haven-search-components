/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDocumentFieldsServiceTest {
    @Mock
    protected ConfigService<HavenSearchCapable> configService;

    @Mock
    protected HavenSearchCapable config;

    protected AbstractDocumentFieldsService documentFieldsService;
    protected int numberOfHardCodedFields;
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
                        .name("SomeField")
                        .build())
                .populateResponseMap("Some other Id", FieldInfo.<String>builder()
                        .name("SomeOtherField")
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
                        .name("SomeField")
                        .build())
                .populateResponseMap("Some other Id", FieldInfo.<String>builder()
                        .name("SomeOtherField")
                        .build())
                .build();
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
        assertThat(documentFieldsService.getPrintFields(Collections.singleton("Some Id")), hasSize(1));
    }

}
