/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldsServiceIT;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchHodConfiguration.class)
public class HodFieldsServiceIT extends AbstractFieldsServiceIT<HodFieldsRequest, HodFieldsRequestBuilder, HodQueryRestrictions, HodErrorException> {
    @Override
    protected HodFieldsRequest createFieldsRequest() {
        return fieldRequestBuilderFactory.getObject()
                .fieldType(FieldTypeParam.Parametric)
                .databases(testUtils.buildQueryRestrictions().getDatabases())
                .maxValues(null)
                .build();
    }
}
