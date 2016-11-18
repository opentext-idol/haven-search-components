/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AbstractParametricValuesServiceIT;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchHodConfiguration.class)
public class HodParametricValuesServiceIT extends AbstractParametricValuesServiceIT<HodParametricRequest, HodFieldsRequest, HodFieldsRequest.HodFieldsRequestBuilder, ResourceIdentifier, HodErrorException> {
    @Override
    protected HodFieldsRequest.HodFieldsRequestBuilder fieldsRequestParams(final HodFieldsRequest.HodFieldsRequestBuilder fieldsRequestBuilder) {
        return fieldsRequestBuilder.databases(testUtils.getDatabases());
    }

    @Override
    @Test(expected = NotImplementedException.class)
    public void getDependentParametricValues() throws HodErrorException {
        super.getDependentParametricValues();
    }
}
