/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.AbstractDocumentServiceIT;
import com.hp.autonomy.searchcomponents.core.search.AutoCorrectException;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.core.search.TypedStateToken;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolDocumentServiceIT extends AbstractDocumentServiceIT<String, IdolSearchResult, AciErrorException> {
    @Test
    public void getStateTokenAndResultCount() {
        final StateTokenAndResultCount stateTokenAndResultCount = documentsService.getStateTokenAndResultCount(integrationTestUtils.buildQueryRestrictions(), 3, false);
        assertThat(stateTokenAndResultCount.getResultCount(), is(greaterThan(0L)));

        final TypedStateToken typedStateToken = stateTokenAndResultCount.getTypedStateToken();
        assertThat(typedStateToken, notNullValue());
        assertThat(typedStateToken.getStateToken(), is(not(isEmptyOrNullString())));
        assertThat(typedStateToken.getType(), is(notNullValue()));
    }

    @Test(expected = AutoCorrectException.class)
    public void queryWithInvalidAutoCorrect() throws AciErrorException {
        final SearchRequest<String> searchRequest = SearchRequest.<String>builder()
                .queryRestrictions(IdolQueryRestrictions.builder()
                        .queryText("XORApple")
                        .fieldText("")
                        .databases(integrationTestUtils.getDatabases())
                        .minDate(null)
                        .maxDate(DateTime.now())
                        .minScore(0)
                        .languageType(null)
                        .anyLanguage(true)
                        .stateMatchIds(Collections.emptyList())
                        .stateDontMatchIds(Collections.emptyList())
                        .build())
                .autoCorrect(true)
                .build();

        documentsService.queryTextIndex(searchRequest);
    }
}
