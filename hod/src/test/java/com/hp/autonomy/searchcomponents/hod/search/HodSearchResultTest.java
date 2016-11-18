/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SearchResultTest;
import org.joda.time.DateTime;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

public class HodSearchResultTest extends SearchResultTest {
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
                .fieldEntry("CUSTOM_FIELD", FieldInfo.builder().name("CUSTOM_FIELD").value("CUSTOM_VALUE").build())
                .date(DateTime.parse("2016-11-16T17:46:00Z"))
                .promotionCategory(PromotionCategory.NONE)
                .domain("PUBLIC_INDEXES")
                .build();
    }
}
