/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AbstractParametricValuesServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HavenSearchIdolConfiguration.class)
public class IdolParametricValuesServiceIT extends AbstractParametricValuesServiceIT<IdolParametricRequest, String, AciErrorException> {
    @Override
    protected IdolParametricRequest createParametricRequest() {
        return new IdolParametricRequest.Builder()
                .setFieldNames(new ArrayList<>(Arrays.asList("AUTHOR", "CATEGORY")))
                .setQueryRestrictions(testUtils.buildQueryRestrictions())
                .build();
    }

    @Override
    protected IdolParametricRequest createNumericParametricRequest() {
        return new IdolParametricRequest.Builder()
                .setFieldNames(new ArrayList<>(Collections.<String>emptyList()))
                .setQueryRestrictions(testUtils.buildQueryRestrictions())
                .build();
    }
}
