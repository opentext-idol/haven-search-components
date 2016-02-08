/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AbstractParametricValuesServiceIT;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.test.HodTestConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HodTestConfiguration.class, HavenSearchHodConfiguration.class})
public class HodParametricValuesServiceIT extends AbstractParametricValuesServiceIT<HodParametricRequest, ResourceIdentifier, HodErrorException> {
    @Override
    protected HodParametricRequest createParametricRequest() {
        return new HodParametricRequest.Builder()
                .setFieldNames(Collections.singleton("WIKIPEDIA_CATEGORY"))
                .setQueryRestrictions(testUtils.buildQueryRestrictions())
                .build();
    }
}
