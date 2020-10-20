/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
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

package com.hp.autonomy.searchcomponents.hod.test;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndexBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictionsBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HodTestUtils implements TestUtils<HodQueryRestrictions> {
    @Autowired
    private ObjectFactory<HodQueryRestrictionsBuilder> queryRestrictionsBuilderFactory;
    @Autowired
    private ObjectFactory<HodGetContentRequestBuilder> getContentRequestBuilderFactory;
    @Autowired
    private ObjectFactory<HodGetContentRequestIndexBuilder> getContentRequestIndexBuilderFactory;

    @Override
    public HodQueryRestrictions buildQueryRestrictions() {
        return queryRestrictionsBuilderFactory.getObject()
                .queryText("*")
                .fieldText("")
                .database(ResourceName.WIKI_ENG)
                .anyLanguage(true)
                .build();
    }

    @Override
    public <RC extends GetContentRequest<?>> RC buildGetContentRequest(final String reference) {
        @SuppressWarnings("unchecked")
        final RC getContentRequest = (RC) getContentRequestBuilderFactory.getObject()
                .indexAndReferences(getContentRequestIndexBuilderFactory.getObject()
                        .index(ResourceName.WIKI_ENG)
                        .reference(reference)
                        .build())
                .build();
        return getContentRequest;
    }
}
