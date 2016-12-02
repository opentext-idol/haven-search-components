/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Simple abstract test class for any object intended to be passed to a HavenSearch controller endpoint complex enough to deserve a builder
 *
 * @param <O> the request object type
 * @param <B> the request object builder type
 */
public abstract class RequestObjectTest<O extends RequestObject<O, B>, B extends RequestObjectBuilder<O, B>>
        extends SimpleRequestObjectTest<O> {
    @SuppressWarnings("ObjectEquality")
    @Test
    public void toBuilder() {
        final O copyViaBuilder = object.toBuilder().build();
        assertFalse(object == copyViaBuilder);
        assertEquals(object, copyViaBuilder);
    }
}
