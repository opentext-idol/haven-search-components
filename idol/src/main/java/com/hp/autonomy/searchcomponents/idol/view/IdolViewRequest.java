/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.hp.autonomy.searchcomponents.core.view.ViewRequest;

/**
 * Options for interacting with {@link IdolViewServerService}
 */
public interface IdolViewRequest extends ViewRequest<String> {
    /**
     * {@inheritDoc}
     */
    @Override
    IdolViewRequestBuilder toBuilder();
}
