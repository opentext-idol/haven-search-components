/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.databases;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.types.idol.Database;
import com.hp.autonomy.types.idol.Databases;
import com.hp.autonomy.types.idol.GetStatusResponseData;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolDatabasesServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private AciResponseJaxbProcessorFactory processorFactory;

    private IdolDatabasesService idolDatabasesService;

    @Before
    public void setUp() {
        idolDatabasesService = new IdolDatabasesService(contentAciService, processorFactory);
    }

    @Test
    public void getDatabases() {
        final GetStatusResponseData responseData = new GetStatusResponseData();
        final Databases databases = new Databases();
        final List<Database> databaseList = databases.getDatabase();
        databaseList.add(mockDatabaseInfo("APublicDatabase", 123, false));
        databaseList.add(mockDatabaseInfo("AnInternalDatabase", 456, true));
        responseData.setDatabases(databases);
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Set<Database> results = idolDatabasesService.getDatabases(new IdolDatabasesRequest());
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
