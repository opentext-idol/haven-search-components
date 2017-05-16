/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SearchResultTest;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
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
public class HodSearchResultTest extends SearchResultTest {
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
                .hasJsonPathArrayValue("$.fieldMap.CUSTOM_FIELD.names", "CUSTOM_FIELD")
                .hasJsonPathStringValue("$.fieldMap.CUSTOM_FIELD.type", "STRING")
                .hasJsonPathBooleanValue("$.fieldMap.CUSTOM_FIELD.advanced", false)
                .hasJsonPathArrayValue("$.fieldMap.CUSTOM_FIELD.names", "CUSTOM_VALUE")
                .hasJsonPathArrayValue("$.fieldMap.DATE_FIELD.names", "DATE_FIELD")
                .hasJsonPathStringValue("$.fieldMap.DATE_FIELD.type", "DATE")
                .hasJsonPathBooleanValue("$.fieldMap.DATE_FIELD.advanced", false)
                .hasJsonPathNumberValue("$.fieldMap.DATE_FIELD.values[0].value", 1479318360000L)
                .hasJsonPathNumberValue("$.date", 1479318360000L)
                .hasJsonPathStringValue("$.promotionCategory", "NONE")
                .hasJsonPathStringValue("$.domain", "PUBLIC_INDEXES");
    }

    @Override
    protected SearchResult constructObject() {
        return HodSearchResult.builder()
                .reference("ABC")
                .index("wiki_eng")
                .title("Fiji")
                .summary("Fiji is an island country")
                .weight(11.7)
                .fieldEntry("CUSTOM_FIELD", FieldInfo.builder()
                        .name(fieldPathNormaliser.normaliseFieldPath("CUSTOM_FIELD"))
                        .value(new FieldValue<>("CUSTOM_VALUE", "Custom Value")).build())
                .fieldEntry("DATE_FIELD", FieldInfo.builder()
                        .name(fieldPathNormaliser.normaliseFieldPath("DATE_FIELD"))
                        .type(FieldType.DATE)
                        .value(new FieldValue<>(ZonedDateTime.parse("2016-11-16T17:46:00Z[UTC]"), "2016-11-16T17:46:00Z"))
                        .build())
                .date(ZonedDateTime.parse("2016-11-16T17:46:00Z[UTC]"))
                .promotionCategory(PromotionCategory.NONE)
                .domain("PUBLIC_INDEXES")
                .build();
    }
}
