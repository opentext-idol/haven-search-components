/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.search.DocumentsService;

/**
 * Contains reusable constants relating to the HoD implementation of {@link DocumentsService}
 */
@SuppressWarnings("WeakerAccess")
public interface HodDocumentsServiceConstants {
    /**
     * Hod limits max results to 2500
      */
    int HOD_MAX_RESULTS = 2500;
}
