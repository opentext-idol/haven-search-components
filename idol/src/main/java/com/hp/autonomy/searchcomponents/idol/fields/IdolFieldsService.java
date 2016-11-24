/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;

/**
 * Idol extension to {@link FieldsService}
 */
@FunctionalInterface
public interface IdolFieldsService extends FieldsService<IdolFieldsRequest, AciErrorException> {
}
