/*
 * Copyright 2015-2017 Open Text.
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
