/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldsServiceIT;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HavenSearchHodConfiguration.class)
public class HodFieldsServiceIT extends AbstractFieldsServiceIT<HodFieldsRequest, ResourceIdentifier, HodErrorException> {
    @Override
    protected HodFieldsRequest createFieldsRequest() {
        return new HodFieldsRequest.Builder()
                .setDatabases(testUtils.getDatabases())
                .setMaxValues(null)
                .build();
    }
}
