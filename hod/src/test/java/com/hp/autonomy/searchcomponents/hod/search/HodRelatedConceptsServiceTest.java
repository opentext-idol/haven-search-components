/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
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
    @Mock
    private HodRelatedConceptsRequest request;
    @Mock
    private HodQueryRestrictions queryRestrictions;

    private HodRelatedConceptsService relatedConceptsService;

    @Before
    public void setUp() {
        when(authenticationRetriever.getPrincipal()).thenReturn(principal);

        relatedConceptsService = new HodRelatedConceptsServiceImpl(findRelatedConceptsService, authenticationRetriever);
    }

    @Test
    public void findRelatedConcepts() throws HodErrorException {
        when(request.getQueryRestrictions()).thenReturn(queryRestrictions);
        relatedConceptsService.findRelatedConcepts(request);
        verify(findRelatedConceptsService).findRelatedConceptsWithText(any(), any());
    }
}

