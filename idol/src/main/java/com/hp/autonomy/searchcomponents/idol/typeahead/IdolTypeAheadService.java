/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Idol extension to {@link TypeAheadService}
 */
@FunctionalInterface
public interface IdolTypeAheadService extends TypeAheadService<AciErrorException> {
    /**
     * The bean name of the qms implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String QMS_TYPE_AHEAD_SERVICE_BEAN_NAME = "qmsTypeAheadService";

    /**
     * The bean name of the term expand implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String TERM_EXPAND_TYPE_AHEAD_SERVICE_BEAN_NAME = "termExpandTypeAheadService";
}
