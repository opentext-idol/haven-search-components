/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;

/**
 * Options for interacting with {@link IdolParametricValuesService}
 */
public interface IdolParametricRequest extends ParametricRequest<IdolQueryRestrictions> {
    /**
     * {@inheritDoc}
     */
    @Override
    IdolParametricRequestBuilder toBuilder();
}
