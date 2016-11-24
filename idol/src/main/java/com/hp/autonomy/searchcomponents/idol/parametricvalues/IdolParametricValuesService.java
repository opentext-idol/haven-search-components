/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;

/**
 * Idol extension to {@link ParametricValuesService}
 */
public interface IdolParametricValuesService extends ParametricValuesService<IdolParametricRequest, String, AciErrorException> {
}
