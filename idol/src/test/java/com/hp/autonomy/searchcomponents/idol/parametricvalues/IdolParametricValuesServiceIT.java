/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AbstractParametricValuesServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolParametricValuesServiceIT extends AbstractParametricValuesServiceIT<IdolParametricRequest, IdolFieldsRequest, IdolFieldsRequest.IdolFieldsRequestBuilder, String, AciErrorException> {
    @Override
    protected FieldsRequest.FieldsRequestBuilder<IdolFieldsRequest> fieldsRequestParams(final IdolFieldsRequest.IdolFieldsRequestBuilder fieldsRequestBuilder) {
        return fieldsRequestBuilder;
    }
}
