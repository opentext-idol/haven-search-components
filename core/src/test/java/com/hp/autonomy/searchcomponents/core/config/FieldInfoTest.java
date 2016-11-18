/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.hp.autonomy.searchcomponents.core.requests.ResponseObjectTest;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

public class FieldInfoTest extends ResponseObjectTest<FieldInfo<String>, FieldInfo.FieldInfoBuilder<String>> {
    @Override
    protected void validateJson(final JsonContent<FieldInfo<String>> jsonContent) throws IOException {
        jsonContent.assertThat().hasJsonPathStringValue("@.id", "someField");
        jsonContent.assertThat().hasJsonPathStringValue("@.type", "STRING");
        jsonContent.assertThat().hasJsonPathBooleanValue("@.advanced", false);
        jsonContent.assertThat().hasJsonPathArrayValue("@.names", "SomeName", "SomeOtherName");
        jsonContent.assertThat().hasJsonPathArrayValue("@.values", "Some value");
    }

    @Override
    protected FieldInfo<String> constructObject() {
        return FieldInfo.<String>builder()
                .id("someField")
                .name("SomeName")
                .name("SomeOtherName")
                .value("Some value")
                .build();
    }

    @Override
    protected String toStringContent() {
        return "type";
    }
}
