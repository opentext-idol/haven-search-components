/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceFlavour;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourceType;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.hod.fields.IndexFieldsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ResourceMapperTest {

    private static final String DOMAIN = "DOMAIN";

    private static final Map<Resource, Set<String>> RESOURCES = ImmutableMap.<Resource, Set<String>>builder()
        .put(new Resource("resource1", null,ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource One"), new HashSet<>(Arrays.asList("author", "category")))
        .put(new Resource("resource2", null,ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Two"), Collections.singleton("category"))
        .put(new Resource("resource3", null,ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Three"), new HashSet<>(Arrays.asList("category", "colour")))
        .put(new Resource("resource4", null,ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Four"), new HashSet<>(Arrays.asList("size", "colour")))
        .put(new Resource("resource5", null,ResourceType.QUERY_PROFILE, ResourceFlavour.CATEGORIZATION, null, "Resource Five"), new HashSet<>(Arrays.asList("author", "category")))
        .build();

    private final ResourceMapper resourceMapper;

    public ResourceMapperTest(final ResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> parameters() throws HodErrorException {
        final IndexFieldsService indexFieldsService = mock(IndexFieldsService.class);

        for(final Map.Entry<Resource, Set<String>> resourceEntry : RESOURCES.entrySet()) {
            when(indexFieldsService.getParametricFields(new ResourceIdentifier(DOMAIN, resourceEntry.getKey().getResource()))).thenAnswer(new Answer<Object>() {
                @Override
                public Object answer(final InvocationOnMock invocation) throws Throwable {
                    TimeUnit.SECONDS.sleep(1);

                    return resourceEntry.getValue();
                }
            });
        }

        return Arrays.asList(
            new Object[]{new ResourceMapperImpl(indexFieldsService)},
            new Object[]{new ForkJoinResourceMapper(indexFieldsService)},
            new Object[]{new ParallelResourceMapper(indexFieldsService)}
        );
    }


    @Test
    public void testResourceMapper() throws HodErrorException {
        final Set<Database> databases = resourceMapper.map(null, RESOURCES.keySet(), DOMAIN);
        final Set<Database> expectation = new HashSet<>();

        for (final Map.Entry<Resource, Set<String>> resource : RESOURCES.entrySet()) {
            expectation.add(new Database.Builder()
                .setName(resource.getKey().getResource())
                .setDisplayName(resource.getKey().getDisplayName())
                .setIndexFields(resource.getValue())
                .setDomain(DOMAIN)
                .setIsPublic(false)
                .build());
        }

        assertThat(databases, is(expectation));
    }

}
