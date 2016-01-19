/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.AbstractDocumentServiceIT;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HavenSearchIdolConfiguration.class)
public class IdolDocumentServiceIT extends AbstractDocumentServiceIT<String, SearchResult, AciErrorException> {
    public IdolDocumentServiceIT() {
        super(Collections.singletonList("Wookiepedia"));
    }

    @Override
    protected QueryRestrictions<String> buildQueryRestrictions() {
        return new IdolQueryRestrictions.Builder()
                .setQueryText("*")
                .setFieldText("")
                .setDatabases(indexes)
                .setAnyLanguage(true)
                .build();
    }
}
