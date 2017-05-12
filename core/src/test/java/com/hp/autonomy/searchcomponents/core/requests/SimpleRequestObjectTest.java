/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Simple abstract test class for any object intended to be passed to a HavenSearch controller endpoint
 */
public abstract class SimpleRequestObjectTest<O extends Serializable> extends SerializableObjectTest<O> {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUpObjectMapper() {
        objectMapper.registerModule(new JodaModule());
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
