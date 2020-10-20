/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
