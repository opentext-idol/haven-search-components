/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequestTest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import com.hp.autonomy.searchcomponents.idol.parametricvalues.IdolParametricRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
@SpringBootTest(classes = CoreTestContext.class, properties = CORE_CLASSES_PROPERTY)
public class IdolParametricRequestTest extends ParametricRequestTest<IdolQueryRestrictions> {
    @Autowired
    private ObjectMapper springObjectMapper;
    @Autowired
    private TagNameFactory tagNameFactory;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        springObjectMapper.addMixIn(QueryRestrictions.class, IdolQueryRestrictionsMixin.class);
    }

    @Override
    protected IdolParametricRequest constructObject() {
        return IdolParametricRequestImpl.builder()
                .fieldNames(Arrays.asList(tagNameFactory.getFieldPath("/DOCUMENT/FIELD1"), tagNameFactory.getFieldPath("/DOCUMENT/FIELD2")))
                .maxValues(10)
                .sort(SortParam.Alphabetical)
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
                .modified(true)
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/parametricvalues/parametricRequest.json"));
    }

    @Override
    protected Object readJson() throws IOException {
        return springObjectMapper.readValue(json(), IdolParametricRequestImpl.class);
    }
}
