/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequestTest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsMixin;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;

import java.io.IOException;
import java.util.Arrays;

public class IdolParametricRequestTest extends ParametricRequestTest<String> {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        objectMapper.addMixIn(QueryRestrictions.class, IdolQueryRestrictionsMixin.class);
    }

    @Override
    protected ParametricRequest<String> constructObject() {
        return IdolParametricRequest.builder()
                .fieldNames(Arrays.asList("field1", "field2"))
                .maxValues(10)
                .sort(SortParam.Alphabetical)
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
                .modified(true)
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/parametricvalues/parametricRequest.json"));
    }
}
