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

package com.hp.autonomy.searchcomponents.core.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.searchcomponents.core.requests.SimpleRequestResponseObjectTest;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
@SpringBootTest(classes = CoreTestContext.class, properties = CORE_CLASSES_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TagNameTest extends SimpleRequestResponseObjectTest<TagName> {
    @Autowired
    private ObjectMapper springObjectMapper;

    @Override
    public void setUpObjectMapper() {
        json = new JacksonTester<>(getClass(), ResolvableType.forClass(TagName.class), springObjectMapper);
    }

    @Override
    protected TagName constructObject() {
        return new TagNameImpl(new FieldPathImpl("/DOCUMENT/FOO_BAR", "FOO_BAR"), "Foo Bar");
    }

    @Override
    protected InputStream json() throws IOException {
        return IOUtils.toInputStream("\"/DOCUMENT/FOO_BAR\"");
    }

    @Override
    protected void validateJson(final JsonContent<TagName> jsonContent) throws IOException {
        jsonContent.assertThat()
                .hasJsonPathStringValue("$.id", "/DOCUMENT/FOO_BAR")
                .hasJsonPathStringValue("$.displayName", "Foo Bar");
    }

    @Override
    protected String toStringContent() {
        return "id";
    }

    @Override
    protected TagName readJson() throws IOException {
        return springObjectMapper.readValue(json(), TagName.class);
    }
}
