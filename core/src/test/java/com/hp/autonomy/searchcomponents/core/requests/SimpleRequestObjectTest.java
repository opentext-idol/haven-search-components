/*
 * Copyright 2015-2017 Open Text.
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

package com.hp.autonomy.searchcomponents.core.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;

import static org.hamcrest.Matchers.equalTo;

/**
 * Simple abstract test class for any object intended to be passed to a HavenSearch controller endpoint
 */
public abstract class SimpleRequestObjectTest<O extends Serializable> extends SerializableObjectTest<O> {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUpObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Json as String to be converted into a Java object
     * Should be an accurate representation of the object returned by {@link #constructObject()}
     *
     * @return json representing the object
     */
    protected abstract String json() throws IOException;

    @Test
    public void fromJson() throws IOException {
        assertThat(readJson(), equalTo(object));
    }

    protected Object readJson() throws IOException {
        return objectMapper.readValue(json(), object.getClass());
    }
}
