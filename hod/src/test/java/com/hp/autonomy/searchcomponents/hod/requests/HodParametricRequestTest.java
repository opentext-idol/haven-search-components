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

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequestTest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
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
public class HodParametricRequestTest extends ParametricRequestTest<HodQueryRestrictions> {
    @Autowired
    private ObjectMapper springObjectMapper;
    @Autowired
    private TagNameFactory tagNameFactory;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        springObjectMapper.addMixIn(QueryRestrictions.class, HodQueryRestrictionsMixin.class);
    }

    @Override
    protected HodParametricRequest constructObject() {
        return HodParametricRequestImpl.builder()
            .fieldNames(Arrays.asList(tagNameFactory.getFieldPath("field1"), tagNameFactory.getFieldPath("field2")))
            .maxValues(10)
            .sort(SortParam.Alphabetical)
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
            .modified(true)
            .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/hod/parametricvalues/parametricRequest.json"));
    }

    @Override
    protected Object readJson() throws IOException {
        return springObjectMapper.readValue(json(), HodParametricRequestImpl.class);
    }
}
