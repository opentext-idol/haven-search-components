/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequestTest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.io.IOUtils;
import org.junit.Before;

import java.io.IOException;
import java.time.ZonedDateTime;

public class IdolQueryRequestTest extends SearchRequestTest<IdolQueryRestrictions> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(QueryRestrictions.class, IdolQueryRestrictionsMixin.class);
    }

    @Override
    protected IdolQueryRequest constructObject() {
        return IdolQueryRequestImpl.<String>builder()
                .queryRestrictions(IdolQueryRestrictionsImpl.builder()
                        .queryText("*")
                        .fieldText("NOT(EMPTY):{FIELD}")
                        .database("Database1")
                        .minDate(ZonedDateTime.parse("2016-11-15T16:07:00Z[UTC]"))
                        .maxDate(ZonedDateTime.parse("2016-11-15T16:07:01Z[UTC]"))
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
