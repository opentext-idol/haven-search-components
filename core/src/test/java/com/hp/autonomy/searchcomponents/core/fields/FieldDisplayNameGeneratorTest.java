/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Optional;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "unused"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestContext.class, properties = CORE_CLASSES_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FieldDisplayNameGeneratorTest {
    @MockBean
    private ConfigService<HavenSearchCapable> configService;
    @Autowired
    private FieldPathNormaliser fieldPathNormaliser;
    @Autowired
    private FieldDisplayNameGenerator fieldDisplayNameGenerator;
    @Mock
    private HavenSearchCapable config;
    @Mock
    private FieldsInfo fieldsInfo;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(config);
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
    }

    @Test
    public void prettifySimpleName() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName(new FieldPathImpl("/DOCUMENT/FOO_BAR", "FOO_BAR")));
    }

    @Test
    public void prettifySimpleNameLeadingUnderscore() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName(new FieldPathImpl("/DOCUMENT/_FOO_BAR", "_FOO_BAR")));
    }

    @Test
    public void prettifySimpleNameInternalUnderscore() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName(new FieldPathImpl("/DOCUMENT/FOO__BAR", "FOO__BAR")));
    }

    @Test
    public void prettifySimpleNameTrailingUnderscore() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName(new FieldPathImpl("/DOCUMENT/FOO_BAR_", "FOO_BAR_")));
    }

    @Test
    public void prettifyCompoundName() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Foo Baz", fieldDisplayNameGenerator.generateDisplayName(new FieldPathImpl("/DOCUMENTS/DOCUMENT/FOO_BAR/FOO_BAZ", "FOO_BAZ")));
    }

    @Test
    public void generateDisplayNameFromConfiguredId() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayNameFromId("foo_bar"));
    }

    @Test
    public void prettifyFieldValue() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Value", fieldDisplayNameGenerator.generateDisplayValue(new FieldPathImpl("/DOCUMENT/FOO_BAR", "FOO_BAR"), "Value", FieldType.STRING));
    }

    @Test
    public void prettifyFieldValueFromId() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        assertEquals("Value", fieldDisplayNameGenerator.generateDisplayValueFromId("fooBar", "Value", FieldType.STRING));
    }

    @Test
    public void generateDisplayNameFromConfig() {
        final FieldPath path = fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/FOO");
        final String id = "foo_bar";
        final String displayName = "Bar";
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(path, FieldInfo.builder()
                .id(id)
                .displayName(displayName)
                .name(path)
                .build())));
        assertEquals(displayName, fieldDisplayNameGenerator.generateDisplayName(path));
    }

    @Test
    public void generateDefaultDisplayNameFromConfig() {
        final FieldPath path = fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/FOO");
        final String id = "foo_bar";
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(path, FieldInfo.builder()
                .id(id)
                .name(path)
                .build())));
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName(path));
    }

    @Test
    public void generateDisplayNameFromConfigById() {
        final String id = "foo_bar";
        final String displayName = "Bar";
        when(fieldsInfo.getFieldConfig()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(id, FieldInfo.builder()
                .id(id)
                .displayName(displayName)
                .build())));
        assertEquals(displayName, fieldDisplayNameGenerator.generateDisplayNameFromId(id));
    }

    @Test
    public void generateDefaultDisplayNameFromConfigById() {
        final String id = "foo_bar";
        when(fieldsInfo.getFieldConfig()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(id, FieldInfo.builder()
                .id(id)
                .build())));
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayNameFromId(id));
    }

    @Test
    public void generateDisplayNameForUnknownFieldById() {
        when(fieldsInfo.getFieldConfig()).thenReturn(new LinkedHashMap<>());
        final String id = "foo_bar";
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayNameFromId(id));
    }

    @Test
    public void generateDisplayValueFromConfig() {
        final FieldPath path = fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/FOO");
        final String value = "Foo";
        final String displayValue = "Bar";
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(path, FieldInfo.builder()
                .name(path)
                .value(new FieldValue<>(value, displayValue))
                .build())));
        assertEquals(displayValue, fieldDisplayNameGenerator.generateDisplayValue(path, value, FieldType.STRING));
    }

    @Test
    public void generateDefaultDisplayValueFromConfig() {
        final FieldPath path = fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/FOO");
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(path, FieldInfo.builder()
                .name(path)
                .build())));
        final String value = "Foo";
        assertEquals(value, fieldDisplayNameGenerator.generateDisplayValue(path, value, FieldType.STRING));
    }

    @Test
    public void generateDisplayValueForUnknownField() {
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>());
        final FieldPath path = fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/FOO");
        final String value = "Foo";
        assertEquals(value, fieldDisplayNameGenerator.generateDisplayValue(path, value, FieldType.STRING));
    }

    @Test
    public void generateDisplayValueForNullValue() {
        final FieldPath path = fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/FOO");
        when(fieldsInfo.getFieldConfigByName()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(path, FieldInfo.builder()
                .name(path)
                .build())));
        assertEquals(null, fieldDisplayNameGenerator.generateDisplayValue(path, null, FieldType.STRING));
    }

    @Test
    public void generateDisplayValueFromConfigById() {
        final String id = "foo_bar";
        final String value = "Foo";
        final String displayValue = "Bar";
        when(fieldsInfo.getFieldConfig()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(id, FieldInfo.builder()
                .id(id)
                .value(new FieldValue<>(value, displayValue))
                .build())));
        assertEquals(displayValue, fieldDisplayNameGenerator.generateDisplayValueFromId(id, value, FieldType.STRING));
    }

    @Test
    public void generateDefaultDisplayValueFromConfigById() {
        final String id = "foo_bar";
        when(fieldsInfo.getFieldConfig()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(id, FieldInfo.builder()
                .id(id)
                .build())));
        final String value = "Foo";
        assertEquals(value, fieldDisplayNameGenerator.generateDisplayValueFromId(id, value, FieldType.STRING));
    }

    @Test
    public void generateDisplayValueForUnknownFieldById() {
        when(fieldsInfo.getFieldConfig()).thenReturn(new LinkedHashMap<>());
        final String id = "foo_bar";
        final String value = "Foo";
        assertEquals(value, fieldDisplayNameGenerator.generateDisplayValueFromId(id, value, FieldType.STRING));
    }

    @Test
    public void generateDisplayValueFromIdForNullValue() {
        final String id = "foo_bar";
        when(fieldsInfo.getFieldConfig()).thenReturn(new LinkedHashMap<>(ImmutableMap.of(id, FieldInfo.builder()
                .id(id)
                .build())));
        assertEquals(null, fieldDisplayNameGenerator.generateDisplayValueFromId(id, null, FieldType.STRING));
    }

    @Test
    public void prettifyFieldPath() {
        final String path = "/x/y/foo_bar";
        assertEquals("Foo Bar", fieldDisplayNameGenerator.prettifyFieldName(path));
    }

    @Test
    public void parseDisplayValueFromConfig() {
        final FieldPath path = fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/FOO");
        final String value = "Foo";
        final String displayValue = "Bar";
        final FieldInfo<Serializable> fieldInfo = FieldInfo.builder()
                .name(path)
                .value(new FieldValue<>(value, displayValue))
                .build();
        assertEquals(displayValue, fieldDisplayNameGenerator.parseDisplayValue(() -> Optional.of(fieldInfo), value));
    }
}
