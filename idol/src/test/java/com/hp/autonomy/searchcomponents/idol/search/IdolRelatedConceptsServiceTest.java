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

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.types.idol.responses.Qs;
import com.hp.autonomy.types.idol.responses.QsElement;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolRelatedConceptsServiceTest {
    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private IdolRelatedConceptsRequest request;

    private IdolRelatedConceptsService idolRelatedConceptsService;

    @Before
    public void setUp() {
        idolRelatedConceptsService = new IdolRelatedConceptsServiceImpl(parameterHandler, queryExecutor);
    }

    @Test
    public void findRelatedConcepts() {
        final QueryResponseData responseData = new QueryResponseData();
        final Qs qs = new Qs();
        qs.getElement().add(new QsElement());
        responseData.setQs(qs);

        when(queryExecutor.executeQuery(any(), any())).thenReturn(responseData);

        final List<QsElement> results = idolRelatedConceptsService.findRelatedConcepts(request);
        assertThat(results, is(not(empty())));
    }
}
