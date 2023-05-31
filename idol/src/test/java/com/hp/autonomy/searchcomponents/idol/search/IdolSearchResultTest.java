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

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SearchResultTest;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.ZonedDateTime;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestContext.class, properties = CORE_CLASSES_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IdolSearchResultTest extends SearchResultTest {
    @Autowired
    private FieldPathNormaliser fieldPathNormaliser;

    @Override
    protected void validateJson(final JsonContent<SearchResult> jsonContent) throws IOException {
        jsonContent.assertThat()
            .hasJsonPathStringValue("$.reference", "ABC")
            .hasJsonPathStringValue("$.index", "wiki_eng")
            .hasJsonPathStringValue("$.title", "Fiji")
            .hasJsonPathStringValue("$.summary", "Fiji is an island country")
            .hasJsonPathNumberValue("$.weight", 11.7)
            .hasJsonPathArrayValue("$.fieldMap.custom_field.names", "CUSTOM_FIELD")
            .hasJsonPathStringValue("$.fieldMap.custom_field.type", "STRING")
            .hasJsonPathBooleanValue("$.fieldMap.custom_field.advanced", false)
            .hasJsonPathArrayValue("$.fieldMap.custom_field.names", "CUSTOM_VALUE")
            .hasJsonPathArrayValue("$.fieldMap.date_field.names", "DATE_FIELD")
            .hasJsonPathStringValue("$.fieldMap.date_field.type", "DATE")
            .hasJsonPathBooleanValue("$.fieldMap.date_field.advanced", false)
            .hasJsonPathNumberValue("$.fieldMap.date_field.values[0].value", 1479318360000L)
            .hasJsonPathNumberValue("$.date", 1479318360000L)
            .hasJsonPathStringValue("$.promotionCategory", "NONE")
            .hasJsonPathStringValue("$.qmsId", "0")
            .hasJsonPathStringValue("$.promotionName", "SomeName");
    }

    @Override
    protected SearchResult constructObject() {
        final CaseInsensitiveMap<String, FieldInfo<?>> fieldMap = new CaseInsensitiveMap<>();
        fieldMap.put("CUSTOM_FIELD", FieldInfo.builder()
            .name(fieldPathNormaliser.normaliseFieldPath("CUSTOM_FIELD"))
            .value(new FieldValue<>("CUSTOM_VALUE", "Custom Value")).build());
        fieldMap.put("DATE_FIELD", FieldInfo.builder()
            .name(fieldPathNormaliser.normaliseFieldPath("DATE_FIELD"))
            .type(FieldType.DATE)
            .value(new FieldValue<>(ZonedDateTime.parse("2016-11-16T17:46:00Z[UTC]"), "2016-11-16T17:46:00Z"))
            .build());

        return IdolSearchResult.builder()
            .reference("ABC")
            .index("wiki_eng")
            .title("Fiji")
            .summary("Fiji is an island country")
            .weight(11.7)
            .fieldMap(fieldMap)
            .date(ZonedDateTime.parse("2016-11-16T17:46:00Z[UTC]"))
            .promotionCategory(PromotionCategory.NONE)
            .qmsId("0")
            .promotionName("SomeName")
            .build();
    }
}
