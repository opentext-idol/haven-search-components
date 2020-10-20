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

package com.hp.autonomy.searchcomponents.hod.custom;

import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesService;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictionsBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import com.hp.autonomy.searchcomponents.hod.test.HodTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
@SpringBootTest(classes = {HavenSearchHodConfiguration.class, HodCustomConfiguration.class, HodCustomComponentConfiguration.class}, properties = {
        CUSTOMISATION_TEST_ID,
        HodTestConfiguration.MOCK_AUTHENTICATION_PROPERTY + "=false",
        HodTestConfiguration.MOCK_AUTHENTICATION_RETRIEVER_PROPERTY + "=false"
})
public class HodCustomisationTest {
    @Autowired
    @Qualifier("customDatabaseService")
    private HodDatabasesService customDatabaseService;
    @Autowired
    @Qualifier("customQueryRestrictionsBuilder")
    private ObjectFactory<HodQueryRestrictionsBuilder> customQueryRestrictionsBuilderFactory;
    @Autowired
    @Qualifier("customDocumentFieldsService")
    private DocumentFieldsService customDocumentFieldsService;
    @Autowired
    @Qualifier("queryTextIndexService")
    private QueryTextIndexService<HodSearchResult> queryTextIndexService;

    @Test
    public void initialiseWithCustomConfiguration() throws HodErrorException {
        assertNotNull(customDatabaseService);
        assertNotNull(customQueryRestrictionsBuilderFactory.getObject());
        assertNotNull(customDocumentFieldsService);
        assertNull(queryTextIndexService.queryTextIndexWithText(null, null)); // verify it is a mock
    }
}
