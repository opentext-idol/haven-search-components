/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.databases;

import com.hp.autonomy.fields.IndexFieldsService;
import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.ListResourcesRequestBuilder;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceFlavour;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourceType;
import com.hp.autonomy.hod.client.api.resource.Resources;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.error.HodError;
import com.hp.autonomy.hod.client.error.HodErrorCode;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForkJoinDatabasesServiceTest {

    private ForkJoinDatabasesService databasesService;

    @Before
    public void setUp() throws HodErrorException {
        final ResourcesService resourcesService = mock(ResourcesService.class);
        final IndexFieldsService indexFieldsService = new SlowThrowingIndexFieldsService();

        //noinspection unchecked
        when(resourcesService.list(argThat(any(TokenProxy.class)), argThat(any(ListResourcesRequestBuilder.class)))).thenReturn(new Resources(
            Arrays.asList(
                new Resource(
                    "resource1",
                    null,
                    ResourceType.CONTENT,
                    ResourceFlavour.EXPLORER,
                    null
                ),
                new Resource(
                    "resource2",
                    null,
                    ResourceType.CONTENT,
                    ResourceFlavour.EXPLORER,
                    null
                ),
                new Resource(
                    "resource3",
                    null,
                    ResourceType.CONTENT,
                    ResourceFlavour.EXPLORER,
                    null
                ),
                new Resource(
                    "resource4",
                    null,
                    ResourceType.CONTENT,
                    ResourceFlavour.EXPLORER,
                    null
                ),
                new Resource(
                    "resource5",
                    null,
                    ResourceType.CONTENT,
                    ResourceFlavour.EXPLORER,
                    null
                )
            ),
            // remaining public indexes omitted for brevity
            Collections.singletonList(new Resource(
                "wiki_eng",
                null,
                ResourceType.CONTENT,
                ResourceFlavour.EXPLORER,
                null
            ))
        ));

        databasesService = new ForkJoinDatabasesService(resourcesService, indexFieldsService);
    }

    @Test(expected = HodErrorException.class)
    public void testThrowsHodErrorExceptionCorrectly() throws HodErrorException {
            //noinspection unchecked
        databasesService.getDatabases(new TokenProxy<EntityType, TokenType.Simple>(EntityType.Combined.INSTANCE, TokenType.Simple.INSTANCE), "domain");
    }

    private static class SlowThrowingIndexFieldsService implements IndexFieldsService {

        @Override
        public Set<String> getParametricFields(final ResourceIdentifier index) throws HodErrorException {
            throw new UnsupportedOperationException("Not needed for test");
        }

        @Override
        public Set<String> getParametricFields(final TokenProxy<?, TokenType.Simple> tokenProxy, final ResourceIdentifier index) throws HodErrorException {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if ("resource3".equals(index.getName())) {
                final HodError hodError = new HodError.Builder().setErrorCode(HodErrorCode.INDEX_NAME_INVALID).build();
                throw new HodErrorException(hodError, 400);
            }
            else {
                return Collections.singleton("author");
            }
        }
    }

}