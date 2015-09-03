/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.parametricvalues;

import com.hp.autonomy.hod.client.error.HodErrorException;

import java.util.Set;

public interface ParametricValuesService {

    Set<ParametricFieldName> getAllParametricValues(ParametricRequest parametricRequest) throws HodErrorException;

}
