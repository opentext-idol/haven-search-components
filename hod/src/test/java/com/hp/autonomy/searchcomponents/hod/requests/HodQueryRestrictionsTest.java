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

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictionsTest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.time.ZonedDateTime;

public class HodQueryRestrictionsTest extends QueryRestrictionsTest<ResourceName> {
    @Override
    protected QueryRestrictions<ResourceName> constructObject() {
        return HodQueryRestrictionsImpl.builder()
            .queryText("*")
            .fieldText("NOT(EMPTY):{FIELD}")
            .database(ResourceName.WIKI_ENG)
            .minDate(ZonedDateTime.parse("2016-11-15T16:07:00Z[UTC]"))
            .maxDate(ZonedDateTime.parse("2016-11-15T16:07:01Z[UTC]"))
            .minScore(5)
            .languageType("englishUtf8")
            .anyLanguage(false)
            .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/hod/search/queryRestrictions.json"));
    }
}
