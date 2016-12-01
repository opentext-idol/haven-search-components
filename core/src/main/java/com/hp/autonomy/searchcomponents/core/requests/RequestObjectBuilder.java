/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.requests;

/**
 * Generic interface enforcing a basic contract for service request object builders.
 *
 * @param <O> The request object type
 * @param <B> The request object builder type
 */
@FunctionalInterface
public interface RequestObjectBuilder<O extends RequestObject<O, B>, B extends RequestObjectBuilder<O, B>> {
    /**
     * Generates a new request object
     *
     * @return the new request object
     */
    O build();
}
