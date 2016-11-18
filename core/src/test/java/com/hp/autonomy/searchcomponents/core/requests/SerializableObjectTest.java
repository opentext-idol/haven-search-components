/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple abstract test class for any object intended to be {@link Serializable}
 *
 * @param <O> the request object type
 */
public abstract class SerializableObjectTest<O extends Serializable> {
    protected O object;

    @Before
    public void setUp() {
        object = constructObject();
    }

    /**
     * Construct an instance of the object for use in tests
     *
     * @return A fully initialised object
     */
    protected abstract O constructObject();

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
    public void toStringTest() {
        assertTrue(object.toString().contains(toStringContent()));
    }
}
