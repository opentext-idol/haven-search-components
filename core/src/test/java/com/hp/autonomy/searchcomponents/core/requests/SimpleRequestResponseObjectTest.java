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
