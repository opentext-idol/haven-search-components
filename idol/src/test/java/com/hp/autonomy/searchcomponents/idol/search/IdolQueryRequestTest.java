/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.SearchRequestTest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;

import java.io.IOException;

public class IdolQueryRequestTest extends SearchRequestTest<String> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(QueryRestrictions.class, IdolQueryRestrictionsMixin.class);
    }

    @Override
    protected QueryRequest<String> constructObject() {
        return QueryRequest.<String>builder()
                .queryRestrictions(IdolQueryRestrictions.builder()
                        .queryText("*")
                        .fieldText("NOT(EMPTY):{FIELD}")
                        .database("Database1")
                        .minDate(DateTime.parse("2016-11-15T16:07:00Z"))
                        .maxDate(DateTime.parse("2016-11-15T16:07:01Z"))
                        .minScore(5)
                        .languageType("englishUtf8")
                        .anyLanguage(false)
                        .stateMatchId("0-ABC")
                        .stateDontMatchId("0-ABD")
                        .build())
                .start(1)
                .maxResults(50)
                .summary(SummaryParam.Concept.name())
                .summaryCharacters(250)
                .sort(SortParam.Alphabetical.name())
                .highlight(true)
                .autoCorrect(true)
                .print(PrintParam.Fields.name())
                .printField("CATEGORY")
                .queryType(QueryRequest.QueryType.MODIFIED)
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/search/searchRequest.json"));
    }
}
