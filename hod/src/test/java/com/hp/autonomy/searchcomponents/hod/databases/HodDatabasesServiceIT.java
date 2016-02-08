/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.databases.AbstractDatabasesServiceIT;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.test.HodTestConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HodTestConfiguration.class, HavenSearchHodConfiguration.class})
public class HodDatabasesServiceIT extends AbstractDatabasesServiceIT<Database, HodDatabasesRequest, HodErrorException> {
    @Override
    protected HodDatabasesRequest createDatabasesRequest() {
        return new HodDatabasesRequest.Builder()
                .setPublicIndexesEnabled(true)
                .build();
    }
}
