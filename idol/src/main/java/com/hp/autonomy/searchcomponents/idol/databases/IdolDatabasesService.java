/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.databases;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.types.idol.responses.Database;

/**
 * Idol extension to {@link DatabasesService}
 */
@FunctionalInterface
public interface IdolDatabasesService extends DatabasesService<Database, IdolDatabasesRequest, AciErrorException> {
}
