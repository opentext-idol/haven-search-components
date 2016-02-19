/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDocumentFieldsServiceTest {
    @Mock
    protected ConfigService<? extends HavenSearchCapable> configService;

    @Mock
    protected HavenSearchCapable config;

    protected AbstractDocumentFieldsService documentFieldsService;
    protected int numberOfHardCodedFields;
    protected FieldsInfo fieldsInfo;

    @Before
    public void setUp() {
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void getPrintFieldsHardcodedOnly() {
        fieldsInfo = new FieldsInfo.Builder().build();
        assertThat(documentFieldsService.getPrintFields(), hasSize(numberOfHardCodedFields));
    }

    @Test
    public void getAllPrintFields() {
        fieldsInfo = new FieldsInfo.Builder()
                .populateResponseMap("Some Id", new FieldInfo<String>("Some Id", "SomeField", null, FieldType.STRING))
                .build();
        assertThat(documentFieldsService.getPrintFields(), hasSize(numberOfHardCodedFields + 1));
    }
}
