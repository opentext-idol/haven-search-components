/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.view;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.view.ViewRequest;

/**
 * Options for interacting with {@link HodViewServerService}
 */
public interface HodViewRequest extends ViewRequest<ResourceIdentifier> {
    /**
     * {@inheritDoc}
     */
    @Override
    HodViewRequestBuilder toBuilder();
}
