/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.core.ResolvableType;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldsInfoTest extends ConfigurationComponentTest<FieldsInfo> {
    @Override
    public void setUp() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(FieldInfo.class, FieldInfoConfigMixins.class);
        json = new JacksonTester<>(getClass(), ResolvableType.forClass(getType()), objectMapper);
    }

    @Override
    protected Class<FieldsInfo> getType() {
        return FieldsInfo.class;
    }

    @Override
    protected FieldsInfo constructComponent() {
        return FieldsInfo.builder()
                .populateResponseMap("someNumberField", FieldInfo.builder().build())
                .populateResponseMap("anotherField", FieldInfo.builder()
                        .name("ANOTHER_FIELD")
                        .advanced(true)
                        .build())
                .build();
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/core/config/fieldsInfo.json"));
    }

    @Override
    protected void validateJson(final JsonContent<FieldsInfo> jsonContent) {
        jsonContent.assertThat().hasEmptyJsonPathValue("@.someNumberField");
        jsonContent.assertThat().hasJsonPathArrayValue("@.anotherField.names", "ANOTHER_FIELD");
        jsonContent.assertThat().hasJsonPathBooleanValue("@.anotherField.advanced", true);
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<FieldsInfo> objectContent) {
        final Map<String, FieldInfo<?>> fieldInfoMap = objectContent.getObject().getFieldConfig();
        assertThat(fieldInfoMap, hasKey("someField"));
        final FieldInfo<?> someField = fieldInfoMap.get("someField");
        assertThat(someField.getNames(), hasSize(2));
        assertEquals(FieldType.STRING, someField.getType());
        assertThat(fieldInfoMap, hasKey("someNumberField"));
        final FieldInfo<?> numberField = fieldInfoMap.get("someNumberField");
        assertThat(numberField.getNames(), hasSize(1));
        assertEquals(FieldType.NUMBER, numberField.getType());
        assertTrue(numberField.isAdvanced());
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<FieldsInfo> objectContent) {
        final Map<String, FieldInfo<?>> fieldInfoMap = objectContent.getObject().getFieldConfig();
        assertThat(fieldInfoMap.keySet(), hasSize(3));
        assertThat(fieldInfoMap, hasKey("someField"));
        final FieldInfo<?> someField = fieldInfoMap.get("someField");
        assertThat(someField.getNames(), hasSize(2));
        assertEquals(FieldType.STRING, someField.getType());
        assertThat(fieldInfoMap, hasKey("someNumberField"));
        final FieldInfo<?> numberField = fieldInfoMap.get("someNumberField");
        assertThat(numberField.getNames(), empty());
        assertEquals(FieldType.STRING, numberField.getType());
        assertFalse(numberField.isAdvanced());
    }

    @Override
    protected void validateString(final String objectAsString) {
        assertTrue(objectAsString.contains("someNumberField"));
    }
}
