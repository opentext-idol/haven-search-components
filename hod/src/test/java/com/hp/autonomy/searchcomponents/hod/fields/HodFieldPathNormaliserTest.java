/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HodFieldPathNormaliserTest {
    private FieldPathNormaliser fieldPathNormaliser;

    @Before
    public void setUp() {
        fieldPathNormaliser = new HodFieldPathNormaliserImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArg() {
        fieldPathNormaliser.normaliseFieldPath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyArg() {
        fieldPathNormaliser.normaliseFieldPath("");
    }

    @Test
    public void anyOldValue() {
        assertEquals("FOO", fieldPathNormaliser.normaliseFieldPath("FOO"));
    }
}
