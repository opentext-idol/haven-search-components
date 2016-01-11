/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.Entity;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HodRelatedConceptsService implements RelatedConceptsService<Entity, ResourceIdentifier, HodErrorException> {

    @Autowired
    private FindRelatedConceptsService findRelatedConceptsService;

    @Override
    @Cacheable(CacheNames.RELATED_CONCEPTS)
    public List<Entity> findRelatedConcepts(final SearchRequest<ResourceIdentifier> request) throws HodErrorException {

        final FindRelatedConceptsRequestBuilder params = new FindRelatedConceptsRequestBuilder()
                .setIndexes(request.getIndex())
                .setFieldText(request.getFieldText());

        return findRelatedConceptsService.findRelatedConceptsWithText(request.getQueryText(), params);
    }
}
