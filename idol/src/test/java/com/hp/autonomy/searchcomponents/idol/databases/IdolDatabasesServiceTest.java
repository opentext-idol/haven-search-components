/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.databases;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.Database;
import com.hp.autonomy.types.idol.responses.Databases;
import com.hp.autonomy.types.idol.responses.GetStatusResponseData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolDatabasesServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private ProcessorFactory processorFactory;

    private IdolDatabasesService idolDatabasesService;

    @Before
    public void setUp() {
        idolDatabasesService = new IdolDatabasesServiceImpl(contentAciService, processorFactory);
    }

    @Test
    public void getDatabases() {
        final GetStatusResponseData responseData = new GetStatusResponseData();
        final Databases databases = new Databases();
        final List<Database> databaseList = databases.getDatabase();
        databaseList.add(mockDatabaseInfo("APublicDatabase", 123, false));
        databaseList.add(mockDatabaseInfo("AnInternalDatabase", 456, true));
        responseData.setDatabases(databases);
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        final Set<Database> results = idolDatabasesService.getDatabases(mock(IdolDatabasesRequest.class));
        assertThat(results, hasSize(1));
    }

    private Database mockDatabaseInfo(final String name, final long documents, final boolean internal) {
        final Database database = new Database();
        database.setName(name);
        database.setDocuments(documents);
        database.setInternal(internal);
        return database;
    }
}
