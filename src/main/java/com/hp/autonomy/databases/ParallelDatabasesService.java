/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.databases;

import com.hp.autonomy.fields.IndexFieldsService;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.ListResourcesRequestBuilder;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceFlavour;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourceType;
import com.hp.autonomy.hod.client.api.resource.Resources;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class ParallelDatabasesService implements DatabasesService {
    private static final Set<ResourceFlavour> CONTENT_FLAVOURS = ResourceFlavour.of(ResourceFlavour.EXPLORER, ResourceFlavour.STANDARD, ResourceFlavour.CUSTOM_FIELDS);

    private final ResourcesService resourcesService;

    private final IndexFieldsService indexFieldsService;

    private final ExecutorService executorService;

    public ParallelDatabasesService(final ResourcesService resourcesService, final IndexFieldsService indexFieldsService) {
        this(resourcesService, indexFieldsService, Executors.newFixedThreadPool(16));
    }

    public ParallelDatabasesService(final ResourcesService resourcesService, final IndexFieldsService indexFieldsService, final ExecutorService executorService) {
        this.executorService = executorService;
        this.resourcesService = resourcesService;
        this.indexFieldsService = indexFieldsService;
    }

    public void destroy() {
        executorService.shutdown();
    }

    @Override
    public Set<Database> getDatabases(final String domain) throws HodErrorException {
        return getDatabases(null, domain);
    }

    @Override
    public Set<Database> getDatabases(final TokenProxy<?, TokenType.Simple> tokenProxy, final String domain) throws HodErrorException {
        final ListResourcesRequestBuilder builder = new ListResourcesRequestBuilder()
            .setTypes(Collections.singleton(ResourceType.CONTENT));

        final Resources resources;

        if (tokenProxy == null) {
            resources = resourcesService.list(builder);
        }
        else {
            resources = resourcesService.list(tokenProxy, builder);
        }

        // Private and public indexes can have the same name. You can't do anything with the public index in this case,
        // so we remove the public index duplicates here.
        final Set<String> privateDatabaseNames = new HashSet<>();

        final List<Resource> privateResources = new ArrayList<>();

        for (final Resource resource : resources.getResources()) {
            if (CONTENT_FLAVOURS.contains(resource.getFlavour())) {
                final String name = resource.getResource();
                privateResources.add(resource);
                privateDatabaseNames.add(name);
            }
        }

        final List<Future<Database>> databaseFutures = new ArrayList<>();

        for (final Resource privateResource : privateResources) {
            databaseFutures.add(executorService.submit(new DatabaseCallable(tokenProxy, privateResource, domain, false)));
        }

        final List<Resource> publicResources = new ArrayList<>();

        for (final Resource resource : resources.getPublicResources()) {
            if (!privateDatabaseNames.contains(resource.getResource())) {
                publicResources.add(resource);
            }
        }

        for (final Resource publicResource : publicResources) {
            databaseFutures.add(executorService.submit(new DatabaseCallable(tokenProxy, publicResource, ResourceIdentifier.PUBLIC_INDEXES_DOMAIN, true)));
        }

        final Set<Database> databases = new HashSet<>();

        for (final Future<Database> databaseFuture : databaseFutures) {
            try {
                databases.add(databaseFuture.get());
            } catch (final InterruptedException e) {
                // preserve interrupted status
                Thread.currentThread().interrupt();
                // anything we return may be incomplete
                throw new IllegalStateException("Interrupted while waiting for parametric fields", e);
            } catch (final ExecutionException e) {
                if (e.getCause() instanceof HodErrorException) {
                    throw (HodErrorException) e.getCause();
                }
                else {
                    throw new IllegalStateException("Error occurred executing task", e.getCause());
                }
            }
        }

        return databases;
    }

    private Database databaseForResource(final TokenProxy<?, TokenType.Simple> tokenProxy, final String name, final String domain, final boolean isPublic) throws HodErrorException {
        final ResourceIdentifier resourceIdentifier = new ResourceIdentifier(domain, name);
        final Set<String> parametricFields;

        if (tokenProxy == null) {
            parametricFields = indexFieldsService.getParametricFields(resourceIdentifier);
        }
        else {
            parametricFields = indexFieldsService.getParametricFields(tokenProxy, resourceIdentifier);
        }

        return new Database.Builder()
            .setName(name)
            .setIsPublic(isPublic)
            .setDomain(domain)
            .setIndexFields(parametricFields)
            .build();
    }

    private class DatabaseCallable implements Callable<Database> {

        private final TokenProxy<?, TokenType.Simple> tokenProxy;
        private final Resource input;
        private final String domain;
        private final boolean isPublic;

        private DatabaseCallable(final TokenProxy<?, TokenType.Simple> tokenProxy, final Resource input, final String domain, final boolean isPublic) {
            this.tokenProxy = tokenProxy;
            this.input = input;
            this.domain = domain;
            this.isPublic = isPublic;
        }

        @Override
        public Database call() throws HodErrorException {
            return databaseForResource(tokenProxy, input.getResource(), domain, isPublic);
        }
    }

}
