/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.ResolvableType;

import java.io.IOException;
import java.io.Serializable;

/**
 * Simple abstract test class for any object returned by a HavenSearch controller endpoint
 */
public abstract class SimpleResponseObjectTest<O extends Serializable> extends SerializableObjectTest<O> {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected JacksonTester<O> json;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        setUpObjectMapper();
    }

    protected void setUpObjectMapper() {
        objectMapper.registerModule(new JodaModule());
        json = new JacksonTester<>(getClass(), ResolvableType.forClass(object.getClass()), objectMapper);
    }

    /**
     * Validation of generated json
     */
    protected abstract void validateJson(JsonContent<O> jsonContent) throws IOException;

    @Test
    public void toJson() throws IOException {
        final JsonContent<O> jsonContent = json.write(object);
        validateJson(jsonContent);
    }
}
