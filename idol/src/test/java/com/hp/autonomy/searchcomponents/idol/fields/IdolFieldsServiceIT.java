/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldsServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolFieldsServiceIT extends AbstractFieldsServiceIT<IdolFieldsRequest, IdolFieldsRequestBuilder, IdolQueryRestrictions, AciErrorException> {
    @Override
    protected IdolFieldsRequest createFieldsRequest() {
        return fieldRequestBuilderFactory.getObject()
                .maxValues(null)
                .build();
    }
}
