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
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.Database;
import com.opentext.idol.types.responses.Databases;
import com.opentext.idol.types.responses.GetStatusResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdolDatabasesServiceTest {
    @Mock
    private AciService contentAciService;

    @Mock
    private ProcessorFactory processorFactory;

    private IdolDatabasesService idolDatabasesService;

    @BeforeEach
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
        when(contentAciService.executeAction(Mockito.<AciParameter>anySet(), any())).thenReturn(responseData);

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
