/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;

/**
 * HoD extension to {@link ParametricValuesService}
 */
public interface HodParametricValuesService extends ParametricValuesService<HodParametricRequest, ResourceIdentifier, HodErrorException> {
}
