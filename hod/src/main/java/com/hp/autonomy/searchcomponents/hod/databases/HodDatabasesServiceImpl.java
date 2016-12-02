/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.resource.ListResourcesRequestBuilder;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourceType;
import com.hp.autonomy.hod.client.api.resource.Resources;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * HoD databases service implementation: retrieves private and public index information by querying HoD for content resources
 */
@Service(DatabasesService.DATABASES_SERVICE_BEAN_NAME)
class HodDatabasesServiceImpl implements HodDatabasesService {
    private final ResourcesService resourcesService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;

    HodDatabasesServiceImpl(final ResourcesService resourcesService, final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever) {
        this.resourcesService = resourcesService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
    }

    @Override
    public Set<Database> getDatabases(final HodDatabasesRequest request) throws HodErrorException {
        final ListResourcesRequestBuilder builder = new ListResourcesRequestBuilder()
                .setTypes(Collections.singleton(ResourceType.CONTENT));

        final Resources resources = resourcesService.list(builder);

        final Set<Database> databases = new TreeSet<>();

        // Private and public indexes can have the same name. You can't do anything with the public index in this case,
        // so we remove the public index duplicates here.
        final Collection<String> privateResourceNames = new HashSet<>();

        final String domain = authenticationInformationRetriever.getPrincipal().getApplication().getDomain();

        resources.getResources().stream().filter(resource -> CONTENT_FLAVOURS.contains(resource.getFlavour())).forEach(resource -> {
            privateResourceNames.add(resource.getResource());
            databases.add(databaseForResource(resource, domain, false));
        });

        if (request.isPublicIndexesEnabled()) {
            databases.addAll(resources.getPublicResources().stream().filter(resource -> !privateResourceNames.contains(resource.getResource())).map(resource -> databaseForResource(resource, ResourceIdentifier.PUBLIC_INDEXES_DOMAIN, true)).collect(Collectors.toList()));
        }

        return databases;
    }

    /**
     * Converts the given resource name to a database
     *
     * @param resource The resource
     * @param domain   The domain of the resource
     * @return A database representation of the resource
     */
    private Database databaseForResource(final Resource resource, final String domain, final boolean isPublic) {
        return Database.builder()
                .name(resource.getResource())
                .displayName(resource.getDisplayName())
                .isPublic(isPublic)
                .domain(domain)
                .build();
    }
}
