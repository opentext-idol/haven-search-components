/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.Entity;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.search.AbstractRelatedConceptsServiceIT;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HavenSearchHodConfiguration.class)
public class HodRelatedConceptsServiceIT extends AbstractRelatedConceptsServiceIT<Entity, ResourceIdentifier, HodErrorException> {
    @Override
    protected RelatedConceptsRequest<ResourceIdentifier> createRelatedConceptsRequest() {
        return new HodRelatedConceptsRequest.Builder()
                .setQueryRestrictions(testUtils.buildQueryRestrictions())
                .setQuerySummaryLength(50)
                .setMaxResults(200)
                .build();
    }
}
