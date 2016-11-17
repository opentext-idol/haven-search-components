/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Contains reusable constants relating to the Idol implementation of {@link TypeAheadService}
 */
@SuppressWarnings("WeakerAccess")
public interface IdolTypeAheadConstants {
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
