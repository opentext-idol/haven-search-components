/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Idol extension to {@link ParametricValuesService}
 */
public interface IdolParametricValuesService extends ParametricValuesService<IdolParametricRequest, IdolQueryRestrictions, AciErrorException> {
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter
        .ofPattern("HH:mm:ss dd/MM/y[ G]", Locale.ENGLISH).withZone(ZoneOffset.UTC);
}
