/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple abstract test class for any object intended to be passed to a HavenSearch controller endpoint
 */
public abstract class SimpleRequestObjectTest<O extends Serializable> {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    O object;

    @Before
    public void setUp() {
        object = constructObject();

        objectMapper.registerModule(new JodaModule());
    }

    /**
     * Construct an instance of the object for use in tests
     *
     * @return A fully initialised object
     */
    protected abstract O constructObject();

    /**
     * Json as String to be converted into a Java object
     * Should be an accurate representation of the object returned by {@link #constructObject()}
     *
     * @return json representing the object
     */
    protected abstract String json() throws IOException;

    /**
     * Expected content when invoking {@link #toString()} on the object
     *
     * @return the expected content
     */
    protected abstract String toStringContent();

    @Test
    public void serializable() {
        final Serializable copy = SerializationUtils.clone(object);
        assertEquals(object, copy);
    }

    @Test
    public void fromJson() throws IOException {
        assertEquals(object, readJson());
    }

    protected Object readJson() throws IOException {
        return objectMapper.readValue(json(), object.getClass());
    }

    @Test
    public void toStringTest() {
        assertTrue(object.toString().contains(toStringContent()));
    }
}
