/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldsServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HavenSearchIdolConfiguration.class)
public class IdolFieldsServiceIT extends AbstractFieldsServiceIT<IdolFieldsRequest, String, AciErrorException> {
    @Override
    protected IdolFieldsRequest createFieldsRequest() {
        return new IdolFieldsRequest.Builder()
                .setMaxValues(null)
                .build();
    }
}
