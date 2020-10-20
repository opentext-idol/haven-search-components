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

import com.hp.autonomy.hod.client.api.resource.*;
import com.hp.autonomy.hod.client.api.textindex.IndexFlavor;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HodDatabasesServiceTest {
    @Mock
    private ResourcesService resourcesService;

    @Mock
    private IndexFlavourService indexFlavourService;

    private DatabasesService<Database, HodDatabasesRequest, HodErrorException> databasesService;

    @Before
    public void setUp() throws HodErrorException {
        final ResourceDetails resource1 = mockResource("PrivateResource1", "PrivateDomain");
        final ResourceDetails resource2 = mockResource("PrivateResource2", "PrivateDomain");
        final ResourceDetails publicIndex = mockResource("PublicResource1", ResourceName.PUBLIC_INDEXES_DOMAIN);

        when(resourcesService.list(any(ListResourcesRequestBuilder.class))).thenReturn(Arrays.asList(resource1, resource2, publicIndex));
        when(resourcesService.list(any(), any(ListResourcesRequestBuilder.class))).thenReturn(Arrays.asList(resource1, resource2, publicIndex));

        when(indexFlavourService.getIndexFlavour(eq(new ResourceUuid(resource1.getResource().getUuid())))).thenReturn(IndexFlavor.JUMBO);
        when(indexFlavourService.getIndexFlavour(eq(new ResourceUuid(resource2.getResource().getUuid())))).thenReturn(IndexFlavor.QUERY_MANIPULATION);
        when(indexFlavourService.getIndexFlavour(eq(new ResourceUuid(publicIndex.getResource().getUuid())))).thenReturn(IndexFlavor.STANDARD);

        databasesService = new HodDatabasesServiceImpl(resourcesService, indexFlavourService);
    }

    @Test
    public void getAllIndexes() throws HodErrorException {
        final HodDatabasesRequest databasesRequest = mock(HodDatabasesRequest.class);
        when(databasesRequest.isPublicIndexesEnabled()).thenReturn(true);

        final Set<Database> results = databasesService.getDatabases(databasesRequest);
        assertThat(results, hasSize(2));
    }

    @Test
    public void getPrivateIndexes() throws HodErrorException {
        final HodDatabasesRequest databasesRequest = mock(HodDatabasesRequest.class);

        final Set<Database> results = databasesService.getDatabases(databasesRequest);
        assertThat(results, hasSize(1));
    }

    private ResourceDetails mockResource(final String name, final String domain) {
        final Resource resource = Resource.builder()
                .uuid(UUID.randomUUID())
                .domain(domain)
                .name(name)
                .build();

        return new ResourceDetails(resource, "description", ResourceType.TEXT_INDEX, null, name);
    }
}
