/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.Sort;
import com.hp.autonomy.hod.client.api.textindex.query.search.Summary;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequestTest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;

import java.io.IOException;

public class HodQueryRequestTest extends SearchRequestTest<HodQueryRestrictions> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(QueryRestrictions.class, HodQueryRestrictionsMixin.class);
    }

    @Override
    protected HodQueryRequest constructObject() {
        return HodQueryRequestImpl.<ResourceIdentifier>builder()
                .queryRestrictions(HodQueryRestrictionsImpl.builder()
                        .queryText("*")
                        .fieldText("NOT(EMPTY):{FIELD}")
                        .database(ResourceIdentifier.WIKI_ENG)
                        .minDate(DateTime.parse("2016-11-15T16:07:00Z"))
                        .maxDate(DateTime.parse("2016-11-15T16:07:01Z"))
                        .minScore(5)
                        .languageType("englishUtf8")
                        .anyLanguage(false)
                        .build())
                .start(1)
                .maxResults(50)
                .summary(Summary.concept)
                .summaryCharacters(250)
                .sort(Sort.relevance)
                .highlight(true)
                .autoCorrect(true)
                .print(Print.fields)
                .printField("CATEGORY")
                .queryType(QueryRequest.QueryType.MODIFIED)
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/hod/search/searchRequest.json"));
    }
}
