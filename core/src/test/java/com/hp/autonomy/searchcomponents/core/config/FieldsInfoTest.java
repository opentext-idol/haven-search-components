/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
@SpringBootTest(classes = CoreTestContext.class, properties = CORE_CLASSES_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FieldsInfoTest extends ConfigurationComponentTest<FieldsInfo> {
    @Autowired
    private FieldPathNormaliser fieldPathNormaliser;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void setUp() {
        objectMapper.addMixIn(FieldInfo.class, FieldInfoConfigMixins.class);
        objectMapper.addMixIn(FieldValue.class, FieldValueConfigMixins.class);
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
                        .name(fieldPathNormaliser.normaliseFieldPath("ANOTHER_FIELD"))
                        .displayName("Another Field")
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
        jsonContent.assertThat().hasJsonPathValue("@.someNumberField");
        jsonContent.assertThat().hasJsonPathArrayValue("@.anotherField.names", "ANOTHER_FIELD");
        jsonContent.assertThat().hasJsonPathStringValue("@.anotherField.displayName", "Another Field");
        jsonContent.assertThat().hasJsonPathBooleanValue("@.anotherField.advanced", true);
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<FieldsInfo> objectContent) {
        final Map<String, FieldInfo<?>> fieldInfoMap = objectContent.getObject().getFieldConfig();
        assertThat(fieldInfoMap, hasKey("someField"));
        final FieldInfo<?> someField = fieldInfoMap.get("someField");
        assertThat(someField.getNames(), hasSize(2));
        assertEquals(FieldType.STRING, someField.getType());
        assertEquals("SomeValue", someField.getValues().get(0).getValue());
        assertEquals("Some Value", someField.getValues().get(0).getDisplayValue());
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
        assertEquals("SomeValue", someField.getValues().get(0).getValue());
        assertEquals("Some Value", someField.getValues().get(0).getDisplayValue());
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
