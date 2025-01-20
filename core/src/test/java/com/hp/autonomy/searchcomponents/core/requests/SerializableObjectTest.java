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

package com.hp.autonomy.searchcomponents.core.requests;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple abstract test class for any object intended to be {@link Serializable}
 *
 * @param <O> the request object type
 */
public abstract class SerializableObjectTest<O extends Serializable> {
    protected O object;

    @BeforeEach
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
