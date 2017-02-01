/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;

import java.util.Collection;

/**
 * Options for interacting with {@link HodFieldsService}
 */
public interface HodFieldsRequest extends FieldsRequest {
    /**
     * The indexes from which to retrieve fields
     *
     * @return The indexes from which to retrieve fields
     */
    Collection<ResourceName> getDatabases();

    /**
     * {@inheritDoc}
     */
    @Override
    HodFieldsRequestBuilder toBuilder();
}
