/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
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

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.textindex.IndexFlavor;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;

import java.util.Set;

/**
 * HoD extension to {@link DatabasesService}
 */
@FunctionalInterface
public interface HodDatabasesService extends DatabasesService<Database, HodDatabasesRequest, HodErrorException> {
    /**
     * The flavours of HoD resource to include in list of indexes
     */
    Set<IndexFlavor> CONTENT_FLAVOURS = IndexFlavor.of(
            IndexFlavor.EXPLORER,
            IndexFlavor.STANDARD,
            IndexFlavor.CUSTOM_FIELDS,
            IndexFlavor.JUMBO
    );
}
