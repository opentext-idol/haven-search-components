/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;

/**
 * Options for interacting with {@link IdolFieldsService}
 */
public interface IdolFieldsRequest extends FieldsRequest {
    /**
     * {@inheritDoc}
     */
    @Override
    IdolFieldsRequestBuilder toBuilder();
}
