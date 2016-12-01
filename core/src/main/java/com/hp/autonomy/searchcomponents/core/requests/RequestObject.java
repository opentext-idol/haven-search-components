/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

import java.io.Serializable;

/**
 * Generic interface enforcing a basic contract for service request objects.
 * All such objects should be Serializable so that the service invocations can be cached.
 * All such methods should also be deserializable from Json
 *
 * @param <O> The request object type
 * @param <B> The request object builder type
 */
@FunctionalInterface
public interface RequestObject<O extends RequestObject<O, B>, B extends RequestObjectBuilder<O, B>> extends Serializable {
    /**
     * Generates a builder form the current request object
     *
     * @return the builder constructed with all the fields of the current request object
     */
    B toBuilder();
}
