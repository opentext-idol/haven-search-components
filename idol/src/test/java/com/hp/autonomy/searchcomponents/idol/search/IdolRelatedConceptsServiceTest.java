/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.opentext.idol.types.responses.Qs;
import com.opentext.idol.types.responses.QsElement;
import com.opentext.idol.types.responses.QueryResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdolRelatedConceptsServiceTest {
    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private QueryExecutor queryExecutor;

    @Mock
    private IdolRelatedConceptsRequest request;

    private IdolRelatedConceptsService idolRelatedConceptsService;

    @BeforeEach
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
