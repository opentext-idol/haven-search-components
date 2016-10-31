/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

public class FieldInfoTest {
    @SuppressWarnings("unused")
    private JacksonTester<FieldInfo<?>> json;

    @Before
    public void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void stringObjectToJson() throws IOException {
        final FieldInfo<?> fieldInfo = FieldInfo.builder()
                .id("someField")
                .name("SomeName")
                .name("SomeOtherName")
                .value("Some value")
                .build();
        final JsonContent<FieldInfo<?>> jsonContent = json.write(fieldInfo);
        jsonContent.assertThat().hasJsonPathStringValue("@.id", "someField");
        jsonContent.assertThat().hasJsonPathStringValue("@.type", "STRING");
        jsonContent.assertThat().hasJsonPathBooleanValue("@.advanced", false);
        jsonContent.assertThat().hasJsonPathArrayValue("@.names", "SomeName", "SomeOtherName");
        jsonContent.assertThat().hasJsonPathArrayValue("@.values", "Some value");
    }

    @Test
    public void numberObjectToJson() throws IOException {
        final FieldInfo<?> fieldInfo = FieldInfo.builder()
                .id("someNumberField")
                .name("SomeNumber")
                .advanced(true)
                .value(123)
                .build();
        final JsonContent<FieldInfo<?>> jsonContent = json.write(fieldInfo);
        jsonContent.assertThat().hasJsonPathStringValue("@.id", "someNumberField");
        jsonContent.assertThat().hasJsonPathStringValue("@.type", "NUMBER");
        jsonContent.assertThat().hasJsonPathBooleanValue("@.advanced", true);
        jsonContent.assertThat().hasJsonPathArrayValue("@.names", "SomeNumber");
        jsonContent.assertThat().hasJsonPathArrayValue("@.values", 123);
    }
}
