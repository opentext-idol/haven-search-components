/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

/**
 * Builder for {@link FieldsRequest}
 */
public interface FieldsRequestBuilder<R extends FieldsRequest, B extends FieldsRequestBuilder<R, B>>
        extends RequestObjectBuilder<FieldsRequest, FieldsRequestBuilder<?, ?>> {
    /**
     * Sets max results to return in fields response
     *
     * @param maxValues Max results to return in fields response
     * @return The builder (for chaining)
     */
    B maxValues(Integer maxValues);

    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
