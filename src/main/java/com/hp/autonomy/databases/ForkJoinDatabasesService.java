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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class ForkJoinDatabasesService implements DatabasesService {
    private static final Set<ResourceFlavour> CONTENT_FLAVOURS = ResourceFlavour.of(ResourceFlavour.EXPLORER, ResourceFlavour.STANDARD, ResourceFlavour.CUSTOM_FIELDS);

    private final ResourcesService resourcesService;

    private final IndexFieldsService indexFieldsService;

    private final ForkJoinPool forkJoinPool;

    public ForkJoinDatabasesService(final ResourcesService resourcesService, final IndexFieldsService indexFieldsService) {
        this(resourcesService, indexFieldsService, new ForkJoinPool());
    }

    public ForkJoinDatabasesService(final ResourcesService resourcesService, final IndexFieldsService indexFieldsService, final ForkJoinPool forkJoinPool) {
        this.resourcesService = resourcesService;
        this.indexFieldsService = indexFieldsService;

        this.forkJoinPool = forkJoinPool;
    }

    public void destroy() {
        forkJoinPool.shutdown();
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

        final DatabaseTask privateTask = new DatabaseTask(tokenProxy, privateResources, domain, false);

        forkJoinPool.submit(privateTask);

        final List<Resource> publicResources = new ArrayList<>();

        for (final Resource resource : resources.getPublicResources()) {
            if (!privateDatabaseNames.contains(resource.getResource())) {
                publicResources.add(resource);
            }
        }

        final DatabaseTask publicTask = new DatabaseTask(tokenProxy, publicResources, ResourceIdentifier.PUBLIC_INDEXES_DOMAIN, true);
        forkJoinPool.submit(publicTask);

        final Set<Database> databases = new HashSet<>();

        try {
            databases.addAll(privateTask.get());
            databases.addAll(publicTask.get());
        } catch (final InterruptedException e) {
            log.error("Interrupted", e);
            throw new IllegalStateException("Interrupted while waiting for databases", e);
        } catch (final ExecutionException e) {
            throw new IllegalStateException("Error occurred executing task", e);
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

    private class DatabaseTask extends RecursiveTask<Set<Database>> {

        private static final long serialVersionUID = -8514152774998360171L;
        private final TokenProxy<?, TokenType.Simple> tokenProxy;
        private final List<Resource> input;
        private final String domain;
        private final boolean isPublic;

        private DatabaseTask(final TokenProxy<?, TokenType.Simple> tokenProxy, final List<Resource> input, final String domain, final boolean isPublic) {
            this.tokenProxy = tokenProxy;
            this.input = input;
            this.domain = domain;
            this.isPublic = isPublic;
        }

        @Override
        protected Set<Database> compute() {
            if (input.isEmpty()) {
                return Collections.emptySet();
            }
            else if(input.size() == 1) {
                final Resource resource = input.get(0);

                final Database database;

                try {
                    database = databaseForResource(tokenProxy, resource.getResource(), domain, isPublic);
                } catch (final HodErrorException e) {
                    completeExceptionally(e);
                    // this value is irrelevant as completeExceptionally will throw a RuntimeException
                    return null;
                }

                return Collections.singleton(database);
            }
            else {
                final int middle = (input.size() / 2); // truncation is OK

                final DatabaseTask left =  new DatabaseTask(tokenProxy, input.subList(0, middle), domain, isPublic);
                left.fork();

                final DatabaseTask right = new DatabaseTask(tokenProxy, input.subList(middle, input.size()), domain, isPublic);
                right.fork();

                final Set<Database> result = new HashSet<>();
                result.addAll(left.join());
                result.addAll(right.join());

                return result;
            }
        }
    }

}
