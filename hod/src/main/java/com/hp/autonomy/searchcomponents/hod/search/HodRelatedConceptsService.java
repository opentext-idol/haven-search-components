/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.caching.CachingConfiguration;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.Entity;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HodRelatedConceptsService implements RelatedConceptsService<Entity, ResourceIdentifier, HodErrorException> {
    private final FindRelatedConceptsService findRelatedConceptsService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationRetriever;

    @Autowired
    public HodRelatedConceptsService(
            final FindRelatedConceptsService findRelatedConceptsService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationRetriever) {
        this.findRelatedConceptsService = findRelatedConceptsService;
        this.authenticationRetriever = authenticationRetriever;
    }

    @Override
    @Cacheable(value = CacheNames.RELATED_CONCEPTS, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    public List<Entity> findRelatedConcepts(final RelatedConceptsRequest<ResourceIdentifier> relatedConceptsRequest) throws HodErrorException {

        final QueryRestrictions<ResourceIdentifier> queryRestrictions = relatedConceptsRequest.getQueryRestrictions();
        final FindRelatedConceptsRequestBuilder params = new FindRelatedConceptsRequestBuilder()
                .setIndexes(queryRestrictions.getDatabases())
                .setFieldText(queryRestrictions.getFieldText())
                .setMinScore(queryRestrictions.getMinScore())
                .setMaxResults(relatedConceptsRequest.getMaxResults())
                .setSecurityInfo(authenticationRetriever.getPrincipal().getSecurityInfo());

        return findRelatedConceptsService.findRelatedConceptsWithText(queryRestrictions.getQueryText(), params);
    }
}
