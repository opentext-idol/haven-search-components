/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.test;

import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndexBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class IdolTestUtils implements TestUtils<IdolQueryRestrictions> {
    private static final String KNOWN_TEST_DATABASE = "Wookiepedia";

    @Autowired
    private ObjectFactory<IdolQueryRestrictionsBuilder> queryRestrictionsBuilderFactory;
    @Autowired
    private ObjectFactory<IdolGetContentRequestBuilder> getContentRequestBuilderFactory;
    @Autowired
    private ObjectFactory<IdolGetContentRequestIndexBuilder> getContentRequestIndexBuilderFactory;

    @Override
    public IdolQueryRestrictions buildQueryRestrictions() {
        return queryRestrictionsBuilderFactory.getObject()
                .queryText("*")
                .fieldText("")
                .database(KNOWN_TEST_DATABASE)
                .minDate(null)
                .maxDate(DateTime.now())
                .minScore(0)
                .languageType(null)
                .anyLanguage(true)
                .stateMatchIds(Collections.emptyList())
                .stateDontMatchIds(Collections.emptyList())
                .build();
    }

    @Override
    public <RC extends GetContentRequest<?>> RC buildGetContentRequest(final String reference) {
        @SuppressWarnings("unchecked")
        final RC getContentRequest = (RC) getContentRequestBuilderFactory.getObject()
                .indexAndReferences(getContentRequestIndexBuilderFactory.getObject()
                        .index(KNOWN_TEST_DATABASE)
                        .reference(reference)
                        .build())
                .build();
        return getContentRequest;
    }
}
