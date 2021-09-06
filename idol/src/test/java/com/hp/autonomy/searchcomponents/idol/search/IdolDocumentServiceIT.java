/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
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

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.*;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolDocumentServiceIT extends AbstractDocumentServiceIT<IdolQueryRequest, IdolSuggestRequest, IdolGetContentRequest, IdolQueryRestrictions, IdolSearchResult, AciErrorException> {
    @Autowired
    private ObjectFactory<IdolQueryRestrictionsBuilder> queryRestrictionsBuilder;

    @Test
    public void getStateTokenAndResultCount() {
        final StateTokenAndResultCount stateTokenAndResultCount = documentsService.getStateTokenAndResultCount(integrationTestUtils.buildQueryRestrictions(), 3, QueryRequest.QueryType.RAW, false);
        assertThat(stateTokenAndResultCount.getResultCount(), is(greaterThan(0L)));

        final TypedStateToken typedStateToken = stateTokenAndResultCount.getTypedStateToken();
        assertThat(typedStateToken, notNullValue());
        assertThat(typedStateToken.getStateToken(), is(not(isEmptyOrNullString())));
        assertThat(typedStateToken.getType(), is(notNullValue()));
    }

    @Test(expected = AutoCorrectException.class)
    public void queryWithInvalidAutoCorrect() throws AciErrorException {
        final IdolQueryRequest queryRequest = queryRequestBuilderFactory.getObject()
            .queryRestrictions(queryRestrictionsBuilder.getObject()
                                   .queryText("XORApple")
                                   .fieldText("")
                                   .databases(integrationTestUtils.buildQueryRestrictions().getDatabases())
                                   .minDate(null)
                                   .maxDate(ZonedDateTime.now())
                                   .minScore(0)
                                   .languageType(null)
                                   .anyLanguage(true)
                                   .stateMatchIds(Collections.emptyList())
                                   .stateDontMatchIds(Collections.emptyList())
                                   .build())
            .autoCorrect(true)
            .build();

        documentsService.queryTextIndex(queryRequest);
    }
}
