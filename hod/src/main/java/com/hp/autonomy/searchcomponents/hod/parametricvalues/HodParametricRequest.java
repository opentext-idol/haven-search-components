/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;

/**
 * Options for interacting with {@link HodParametricValuesService}
 */
public interface HodParametricRequest extends ParametricRequest<HodQueryRestrictions> {
    /**
     * {@inheritDoc}
     */
    @Override
    HodParametricRequestBuilder toBuilder();
}
