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
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class HodDatabasesService implements DatabasesService<Database, HodDatabasesRequest, HodErrorException> {
    protected static final Set<ResourceFlavour> CONTENT_FLAVOURS = ResourceFlavour.of(ResourceFlavour.EXPLORER, ResourceFlavour.STANDARD, ResourceFlavour.CUSTOM_FIELDS);

    protected final ResourcesService resourcesService;
    private final AuthenticationInformationRetriever<HodAuthentication> authenticationInformationRetriever;

    public HodDatabasesService(final ResourcesService resourcesService, final AuthenticationInformationRetriever<HodAuthentication> authenticationInformationRetriever) {
        this.resourcesService = resourcesService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
    }

    @Override
    @Cacheable(CacheNames.DATABASES)
    public Set<Database> getDatabases(final HodDatabasesRequest request) throws HodErrorException {
        final ListResourcesRequestBuilder builder = new ListResourcesRequestBuilder()
                .setTypes(Collections.singleton(ResourceType.CONTENT));

        final Resources resources = resourcesService.list(builder);

        final Set<Database> databases = new TreeSet<>();

        // Private and public indexes can have the same name. You can't do anything with the public index in this case,
        // so we remove the public index duplicates here.
        final Collection<String> privateResourceNames = new HashSet<>();

        final HodAuthentication auth = authenticationInformationRetriever.getAuthentication();
        final String domain = auth.getPrincipal().getApplication().getDomain();

        for (final Resource resource : resources.getResources()) {
            if (CONTENT_FLAVOURS.contains(resource.getFlavour())) {
                privateResourceNames.add(resource.getResource());

                databases.add(databaseForResource(resource, domain, false));
            }
        }

        if (request.isPublicIndexesEnabled()) {
            for (final Resource resource : resources.getPublicResources()) {
                if (!privateResourceNames.contains(resource.getResource())) {
                    databases.add(databaseForResource(resource, ResourceIdentifier.PUBLIC_INDEXES_DOMAIN, true));
                }
            }
        }

        return databases;
    }

    /**
     * Converts the given resource name to a database
     *
     * @param resource The resource
     * @param domain   The domain of the resource
     * @return A database representation of the resource
     * @throws HodErrorException
     */
    protected Database databaseForResource(final Resource resource, final String domain, final boolean isPublic) throws HodErrorException {
        return new Database.Builder()
                .setName(resource.getResource())
                .setDisplayName(resource.getDisplayName())
                .setPublic(isPublic)
                .setDomain(domain)
                .build();
    }
}
