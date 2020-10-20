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

package com.hp.autonomy.searchcomponents.idol.test;

import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndexBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collections;

@SuppressWarnings("WeakerAccess")
@Component
public class IdolTestUtils implements TestUtils<IdolQueryRestrictions> {
    public static final String TEST_DATABASE_PROPERTY = "test.database";
    public static final String DEFAULT_TEST_DATABASE = "Wookiepedia";

    @Autowired
    private ObjectFactory<IdolQueryRestrictionsBuilder> queryRestrictionsBuilderFactory;
    @Autowired
    private ObjectFactory<IdolGetContentRequestBuilder> getContentRequestBuilderFactory;
    @Autowired
    private ObjectFactory<IdolGetContentRequestIndexBuilder> getContentRequestIndexBuilderFactory;
    @Autowired
    private Environment environment;

    @Override
    public IdolQueryRestrictions buildQueryRestrictions() {
        return queryRestrictionsBuilderFactory.getObject()
            .queryText("*")
            .fieldText("")
            .database(environment.getProperty(TEST_DATABASE_PROPERTY, DEFAULT_TEST_DATABASE))
            .minDate(null)
            .maxDate(ZonedDateTime.now())
            .minScore(0)
            .languageType(null)
            .anyLanguage(true)
            .stateMatchIds(Collections.emptyList())
            .stateDontMatchIds(Collections.emptyList())
            .build();
    }

    @Override
    public <RC extends GetContentRequest<?>> RC buildGetContentRequest(final String reference) {
        @SuppressWarnings("unchecked") final RC getContentRequest = (RC)getContentRequestBuilderFactory.getObject()
            .indexAndReferences(getContentRequestIndexBuilderFactory.getObject()
                                    .index(environment.getProperty(TEST_DATABASE_PROPERTY, DEFAULT_TEST_DATABASE))
                                    .reference(reference)
                                    .build())
            .build();
        return getContentRequest;
    }
}
