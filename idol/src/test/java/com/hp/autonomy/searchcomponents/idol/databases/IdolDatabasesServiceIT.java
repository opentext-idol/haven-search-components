/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.databases;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.databases.AbstractDatabasesServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.types.idol.responses.Database;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolDatabasesServiceIT extends AbstractDatabasesServiceIT<Database, IdolDatabasesRequest, AciErrorException> {
    @Override
    protected IdolDatabasesRequest createDatabasesRequest() {
        return new IdolDatabasesRequest();
    }
}
