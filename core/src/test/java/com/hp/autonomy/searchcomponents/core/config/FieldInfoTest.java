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

import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.requests.ResponseObjectTest;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestContext.class, properties = CORE_CLASSES_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FieldInfoTest extends ResponseObjectTest<FieldInfo<String>, FieldInfo.FieldInfoBuilder<String>> {
    @Autowired
    private FieldPathNormaliser fieldPathNormaliser;

    @Override
    protected void validateJson(final JsonContent<FieldInfo<String>> jsonContent) throws IOException {
        jsonContent.assertThat().hasJsonPathStringValue("@.id", "someField");
        jsonContent.assertThat().hasJsonPathStringValue("@.displayName", "Some Field");
        jsonContent.assertThat().hasJsonPathStringValue("@.type", "STRING");
        jsonContent.assertThat().hasJsonPathBooleanValue("@.advanced", false);
        jsonContent.assertThat().hasJsonPathArrayValue("@.names", "SomeName", "SomeOtherName");
        jsonContent.assertThat().hasJsonPathStringValue("@.values[0].value", "Some value");
        jsonContent.assertThat().hasJsonPathStringValue("@.values[0].displayValue", "Some Value");
    }

    @Override
    protected FieldInfo<String> constructObject() {
        return FieldInfo.<String>builder()
                .id("someField")
                .name(fieldPathNormaliser.normaliseFieldPath("SomeName"))
                .name(fieldPathNormaliser.normaliseFieldPath("SomeOtherName"))
                .displayName("Some Name")
                .value(new FieldValue<>("Some value", "Some Value"))
                .build();
    }

    @Override
    protected String toStringContent() {
        return "type";
    }
}
