/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.custom;

import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.databases.Database;
import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HavenSearchHodConfiguration.class, HodCustomConfiguration.class, HodCustomComponentConfiguration.class}, properties = CUSTOMISATION_TEST_ID)
public class HodCustomisationTest {
    @Autowired
    @Qualifier("customDatabaseService")
    private DatabasesService<Database, HodDatabasesRequest, HodErrorException> customDatabaseService;
    @Autowired
    @Qualifier("customDocumentFieldsService")
    private DocumentFieldsService customDocumentFieldsService;
    @Autowired
    @Qualifier("queryTextIndexService")
    private QueryTextIndexService<HodSearchResult> queryTextIndexService;

    @Test
    public void initialiseWithCustomConfiguration() throws HodErrorException {
        assertNotNull(customDatabaseService);
        assertNotNull(customDocumentFieldsService);
        assertNull(queryTextIndexService.queryTextIndexWithText(null, null)); // verify it is a mock
    }
}
