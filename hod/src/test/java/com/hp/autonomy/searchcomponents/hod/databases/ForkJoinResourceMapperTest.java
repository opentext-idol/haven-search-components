/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceFlavour;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourceType;
import com.hp.autonomy.hod.client.error.HodError;
import com.hp.autonomy.hod.client.error.HodErrorCode;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.searchcomponents.hod.fields.IndexFieldsService;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ForkJoinResourceMapperTest {

    private ForkJoinResourceMapper resourceMapper;
    private Set<Resource> resourceNames;

    @Before
    public void setUp() throws HodErrorException {
        final IndexFieldsService indexFieldsService = new SlowThrowingIndexFieldsService();

        resourceNames = new HashSet<>(Arrays.asList(
            new Resource("resource1", null, ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource One"),
            new Resource("resource2", null, ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Two"),
            new Resource("resource3", null, ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Three"),
            new Resource("resource4", null, ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Four"),
            new Resource("resource5", null, ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Five")
        ));

        resourceMapper = new ForkJoinResourceMapper(indexFieldsService);
    }

    @Test(expected = HodErrorException.class)
    public void testThrowsHodErrorExceptionCorrectly() throws HodErrorException {
        //noinspection unchecked
        resourceMapper.map(new TokenProxy<EntityType, TokenType.Simple>(EntityType.Combined.INSTANCE, TokenType.Simple.INSTANCE), resourceNames, "domain");
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