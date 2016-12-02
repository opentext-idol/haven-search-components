/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;

/**
 * HoD extension to {@link FieldsService}
 */
@FunctionalInterface
public interface HodFieldsService extends FieldsService<HodFieldsRequest, HodErrorException> {
}
