/*
 * Copyright 2015-2017 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequestTest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolSuggestRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.io.IOUtils;
import org.junit.Before;

import java.io.IOException;
import java.time.ZonedDateTime;

public class IdolSuggestRequestTest extends SuggestRequestTest<IdolQueryRestrictions> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(QueryRestrictions.class, IdolQueryRestrictionsMixin.class);
    }

    @Override
    protected IdolSuggestRequest constructObject() {
        return IdolSuggestRequestImpl.<String>builder()
            .reference("REFERENCE")
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
            .print(PrintParam.Fields.name())
            .printField("CATEGORY")
            .referenceField("ref")
            .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/search/suggestRequest.json"));
    }
}
