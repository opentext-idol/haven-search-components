/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictionsTest;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.IOException;

public class HodQueryRestrictionsTest extends QueryRestrictionsTest<ResourceIdentifier> {
    @Override
    protected QueryRestrictions<ResourceIdentifier> constructObject() {
        return HodQueryRestrictions.builder()
                .queryText("*")
                .fieldText("NOT(EMPTY):{FIELD}")
                .database(ResourceIdentifier.WIKI_ENG)
                .minDate(DateTime.parse("2016-11-15T16:07:00Z"))
                .maxDate(DateTime.parse("2016-11-15T16:07:01Z"))
                .minScore(5)
                .languageType("englishUtf8")
                .anyLanguage(false)
                .stateMatchId("0-ABC")
                .stateDontMatchId("0-ABD")
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/hod/search/queryRestrictions.json"));
    }
}
