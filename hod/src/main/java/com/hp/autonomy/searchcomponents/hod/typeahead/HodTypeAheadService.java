/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.typeahead;

import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;

/**
 * HoD extension to {@link TypeAheadService}
 */
@FunctionalInterface
public interface HodTypeAheadService extends TypeAheadService<HodErrorException> {
}
