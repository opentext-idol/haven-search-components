/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;

/**
 * Simple abstract test class for any object intended to be passed to or returned from a HavenSearch controller endpoint
 */
public abstract class SimpleRequestResponseObjectTest<O extends Serializable> extends SimpleResponseObjectTest<O> {
    /**
     * Json as String to be converted into a Java object
     * Should be an accurate representation of the object returned by {@link #constructObject()}
     *
     * @return json representing the object
     */
    protected abstract InputStream json() throws IOException;

    @Test
    public void fromJson() throws IOException {
        assertEquals(object, readJson());
    }

    protected O readJson() throws IOException {
        return json.readObject(json());
    }
}
