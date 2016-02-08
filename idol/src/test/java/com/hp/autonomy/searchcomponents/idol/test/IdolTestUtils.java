/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.test;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class IdolTestUtils implements TestUtils<String> {
    @Override
    public List<String> getDatabases() {
        return Collections.singletonList("Wookiepedia");
    }

    @Override
    public QueryRestrictions<String> buildQueryRestrictions() {
        return new IdolQueryRestrictions.Builder()
                .setQueryText("*")
                .setFieldText("")
                .setDatabases(getDatabases())
                .setMinDate(null)
                .setMaxDate(DateTime.now())
                .setLanguageType(null)
                .setAnyLanguage(true)
                .setStateMatchId(Collections.<String>emptyList())
                .setStateDontMatchId(Collections.<String>emptyList())
                .build();
    }
}
