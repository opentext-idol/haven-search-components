/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.databases;

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
import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
public class DatabasesServiceImpl implements DatabasesService {
    private static final Set<ResourceFlavour> CONTENT_FLAVOURS = ResourceFlavour.of(ResourceFlavour.EXPLORER, ResourceFlavour.STANDARD, ResourceFlavour.CUSTOM_FIELDS);

    private final ResourcesService resourcesService;

    private final ResourceMapper resourceMapper;

    public DatabasesServiceImpl(final ResourcesService resourcesService, final ResourceMapper resourceMapper) {
        this.resourcesService = resourcesService;
        this.resourceMapper = resourceMapper;
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

        final Set<Database> databases = new HashSet<>();

        // Private and public indexes can have the same name. You can't do anything with the public index in this case,
        // so we remove the public index duplicates here.
        final Set<String> privateResourceNames = new HashSet<>();

        for (final Resource resource : resources.getResources()) {
            if (CONTENT_FLAVOURS.contains(resource.getFlavour())) {
                final String name = resource.getResource();
                privateResourceNames.add(name);
            }
        }

        databases.addAll(resourceMapper.map(tokenProxy, privateResourceNames, domain));

        final Set<String> publicResourceNames = new HashSet<>();

        for (final Resource resource : resources.getPublicResources()) {
            if (!privateResourceNames.contains(resource.getResource())) {
                publicResourceNames.add(resource.getResource());
            }
        }

        databases.addAll(resourceMapper.map(tokenProxy, publicResourceNames, ResourceIdentifier.PUBLIC_INDEXES_DOMAIN));

        return databases;
    }


}
