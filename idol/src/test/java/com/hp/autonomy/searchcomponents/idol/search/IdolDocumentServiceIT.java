/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.AbstractDocumentServiceIT;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.core.search.TypedStateToken;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HavenSearchIdolConfiguration.class)
public class IdolDocumentServiceIT extends AbstractDocumentServiceIT<String, IdolSearchResult, AciErrorException> {
    @Test
    public void getStateTokenAndResultCount() {
        final StateTokenAndResultCount stateTokenAndResultCount = documentsService.getStateTokenAndResultCount(integrationTestUtils.buildQueryRestrictions(), 3, false);
        assertThat(stateTokenAndResultCount.getResultCount(), is(greaterThan(0L)));

        final TypedStateToken typedStateToken = stateTokenAndResultCount.getTypedStateToken();
        assertThat(typedStateToken, notNullValue());
        assertThat(typedStateToken.getStateToken(), is(not(isEmptyOrNullString())));
        assertThat(typedStateToken.getType(), is(notNullValue()));
    }
}
