/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.searchcomponents.hod.fields.IndexFieldsService;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of ResourceMapper
 */
public class ResourceMapperImpl extends AbstractResourceMapper {
    public ResourceMapperImpl(final IndexFieldsService indexFieldsService) {
        super(indexFieldsService);
    }

    @Override
    public Set<Database> map(final TokenProxy<?, TokenType.Simple> tokenProxy, final Set<Resource> resources, final String domain) throws HodErrorException {
        final Set<Database> databases = new HashSet<>();

        for (final Resource resource : resources) {
            databases.add(databaseForResource(tokenProxy, resource, domain));
        }

        return databases;
    }
}
