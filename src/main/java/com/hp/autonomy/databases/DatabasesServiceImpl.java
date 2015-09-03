/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.databases;

import com.hp.autonomy.hod.client.api.resource.ListResourcesRequestBuilder;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceFlavour;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourceType;
import com.hp.autonomy.hod.client.api.resource.Resources;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.fields.IndexFieldsService;
import lombok.Data;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Data
public class DatabasesServiceImpl implements DatabasesService {
    private static final EnumSet<ResourceFlavour> CONTENT_FLAVOURS = EnumSet.of(ResourceFlavour.explorer, ResourceFlavour.standard, ResourceFlavour.custom_fields);

    private final String domain;

    private final ResourcesService resourcesService;

    private final IndexFieldsService indexFieldsService;

    public DatabasesServiceImpl(final String domain, final ResourcesService resourcesService, final IndexFieldsService indexFieldsService) {
        this.domain = domain;
        this.resourcesService = resourcesService;
        this.indexFieldsService = indexFieldsService;
    }

    @Override
    public Set<Database> getDatabases() throws HodErrorException {
        final ListResourcesRequestBuilder builder = new ListResourcesRequestBuilder()
            .setTypes(Collections.singleton(ResourceType.content));

        final Resources resources = resourcesService.list(builder);
        final Set<Database> databases = new HashSet<>();

        // Private and public indexes can have the same name. You can't do anything with the public index in this case,
        // so we remove the public index duplicates here.
        final Set<String> privateDatabaseNames = new HashSet<>();

        for (final Resource resource : resources.getResources()) {
            if (CONTENT_FLAVOURS.contains(resource.getFlavour())) {
                final String name = resource.getResource();
                databases.add(databaseForResource(name, domain, false));
                privateDatabaseNames.add(name);
            }
        }

        for (final Resource resource : resources.getPublicResources()) {
            if (!privateDatabaseNames.contains(resource.getResource())) {
                databases.add(databaseForResource(resource.getResource(), ResourceIdentifier.PUBLIC_INDEXES_DOMAIN, true));
            }
        }

        return databases;
    }

    private Database databaseForResource(final String name, final String domain, final boolean isPublic) throws HodErrorException {
        return new Database.Builder()
            .setName(name)
            .setIsPublic(isPublic)
            .setDomain(domain)
            .setIndexFields(indexFieldsService.getParametricFields(new ResourceIdentifier(domain, name)))
            .build();
    }

}
