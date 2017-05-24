/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.Sort;
import com.hp.autonomy.hod.client.api.textindex.query.search.Summary;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequestTest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import org.apache.commons.io.IOUtils;
import org.junit.Before;

import java.io.IOException;
import java.time.ZonedDateTime;

public class HodSuggestRequestTest extends SuggestRequestTest<HodQueryRestrictions> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(QueryRestrictions.class, HodQueryRestrictionsMixin.class);
    }

    @Override
    protected SuggestRequest<HodQueryRestrictions> constructObject() {
        return HodSuggestRequestImpl.<ResourceName>builder()
            .reference("REFERENCE")
            .queryRestrictions(HodQueryRestrictionsImpl.builder()
                                   .queryText("*")
                                   .fieldText("NOT(EMPTY):{FIELD}")
                                   .database(ResourceName.WIKI_ENG)
                                   .minDate(ZonedDateTime.parse("2016-11-15T16:07:00Z[UTC]"))
                                   .maxDate(ZonedDateTime.parse("2016-11-15T16:07:01Z[UTC]"))
                                   .minScore(5)
                                   .languageType("englishUtf8")
                                   .anyLanguage(false)
                                   .build())
            .start(1)
            .maxResults(50)
            .summary(Summary.concept.name())
            .summaryCharacters(250)
            .sort(Sort.relevance.name())
            .highlight(true)
            .print(Print.fields.name())
            .printField("CATEGORY")
            .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/hod/search/suggestRequest.json"));
    }
}
