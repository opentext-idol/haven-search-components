/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.resource.ListResourcesRequestBuilder;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceFlavour;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourceType;
import com.hp.autonomy.hod.client.api.resource.Resources;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HodDatabasesServiceTest {
    @Mock
    private ResourcesService resourcesService;

    @Mock
    private AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;

    @Mock
    private HodAuthenticationPrincipal hodAuthenticationPrincipal;

    private DatabasesService<Database, HodDatabasesRequest, HodErrorException> databasesService;

    @Before
    public void setUp() {
        databasesService = new HodDatabasesService(resourcesService, authenticationInformationRetriever);
    }

    @Before
    public void mocks() throws HodErrorException {
        when(hodAuthenticationPrincipal.getApplication()).thenReturn(new ResourceIdentifier("SomeDomain", "SomeIndex"));
        when(authenticationInformationRetriever.getPrincipal()).thenReturn(hodAuthenticationPrincipal);

        mockListResourcesResponse();
    }

    @Test
    public void getAllIndexes() throws HodErrorException {
        final HodDatabasesRequest databasesRequest = new HodDatabasesRequest.Builder()
                .setPublicIndexesEnabled(true)
                .build();

        final Set<Database> results = databasesService.getDatabases(databasesRequest);
        assertThat(results, hasSize(2));
    }

    @Test
    public void getPrivateIndexes() throws HodErrorException {
        final HodDatabasesRequest databasesRequest = new HodDatabasesRequest.Builder()
                .setPublicIndexesEnabled(false)
                .build();

        final Set<Database> results = databasesService.getDatabases(databasesRequest);
        assertThat(results, hasSize(1));
    }

    private void mockListResourcesResponse() throws HodErrorException {
        final Resource resource1 = mockResource("PrivateResource1", ResourceFlavour.STANDARD);
        final Resource resource2 = mockResource("PrivateResource2", ResourceFlavour.WEB_CLOUD);
        final List<Resource> privateResources = Arrays.asList(resource1, resource2);
        final List<Resource> publicResources = Collections.singletonList(mockResource("PublicResource1", ResourceFlavour.STANDARD));
        when(resourcesService.list(any(ListResourcesRequestBuilder.class))).thenReturn(new Resources(privateResources, publicResources));
        when(resourcesService.list(any(), any(ListResourcesRequestBuilder.class))).thenReturn(new Resources(privateResources, publicResources));
    }

    private Resource mockResource(final String name, final ResourceFlavour resourceFlavour) {
        return new Resource(name, "Some Description", ResourceType.CONTENT, resourceFlavour, null, name);
    }
}
