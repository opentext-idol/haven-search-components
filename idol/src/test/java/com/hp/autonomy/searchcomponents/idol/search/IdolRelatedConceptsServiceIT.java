/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.AbstractRelatedConceptsServiceIT;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.types.idol.QsElement;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolRelatedConceptsServiceIT extends AbstractRelatedConceptsServiceIT<QsElement, String, AciErrorException> {
    @Override
    protected RelatedConceptsRequest<String> createRelatedConceptsRequest() {
        return new IdolRelatedConceptsRequest.Builder()
                .setQueryRestrictions(testUtils.buildQueryRestrictions())
                .setQuerySummaryLength(50)
                .build();
    }
}
