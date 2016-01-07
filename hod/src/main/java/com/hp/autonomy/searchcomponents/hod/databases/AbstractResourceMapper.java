/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.searchcomponents.hod.fields.IndexFieldsService;

import java.util.Set;

/**
 * Abstract base class for {@link ResourceMapper}
 */
public abstract class AbstractResourceMapper implements ResourceMapper {

    private final IndexFieldsService indexFieldsService;

    protected AbstractResourceMapper(final IndexFieldsService indexFieldsService) {
        this.indexFieldsService = indexFieldsService;
    }

    /**
     * Converts the given resource name to a database
     * @param tokenProxy The token proxy to use to retrieve parametric fields
     * @param resource The resource
     * @param domain The domain of the resource
     * @return A database representation of the resource
     * @throws HodErrorException
     */
    protected Database databaseForResource(final TokenProxy<?, TokenType.Simple> tokenProxy, final Resource resource, final String domain) throws HodErrorException {
        final ResourceIdentifier resourceIdentifier = new ResourceIdentifier(domain, resource.getResource());
        final Set<String> parametricFields;

        if (tokenProxy == null) {
            parametricFields = indexFieldsService.getParametricFields(resourceIdentifier);
        }
        else {
            parametricFields = indexFieldsService.getParametricFields(tokenProxy, resourceIdentifier);
        }

        return new Database.Builder()
            .setName(resource.getResource())
            .setDisplayName(resource.getDisplayName())
            .setIsPublic(ResourceIdentifier.PUBLIC_INDEXES_DOMAIN.equals(domain))
            .setDomain(domain)
            .setIndexFields(parametricFields)
            .build();
    }

}
