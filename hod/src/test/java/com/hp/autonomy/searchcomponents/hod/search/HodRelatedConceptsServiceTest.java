/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.Entity;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HodRelatedConceptsServiceTest {
    @Mock
    private FindRelatedConceptsService findRelatedConceptsService;
    @Mock
    private AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationRetriever;
    @Mock
    private HodAuthenticationPrincipal principal;

    private RelatedConceptsService<Entity, ResourceIdentifier, HodErrorException> relatedConceptsService;

    @Before
    public void setUp() {
        when(authenticationRetriever.getPrincipal()).thenReturn(principal);

        relatedConceptsService = new HodRelatedConceptsService(findRelatedConceptsService, authenticationRetriever);
    }

    @Test
    public void findRelatedConcepts() throws HodErrorException {
        final RelatedConceptsRequest<ResourceIdentifier> relatedConceptsRequest = HodRelatedConceptsRequest.<ResourceIdentifier>builder()
                .queryRestrictions(HodQueryRestrictions.builder()
                        .build())
                .build();
        relatedConceptsService.findRelatedConcepts(relatedConceptsRequest);
        verify(findRelatedConceptsService).findRelatedConceptsWithText(any(), any());
    }
}

