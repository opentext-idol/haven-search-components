/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hp.autonomy.searchcomponents.core.requests.SimpleRequestObjectTest;

import java.io.IOException;

public class NumericBucketingParamsTest extends SimpleRequestObjectTest<BucketingParams<Double>> {
    @Override
    protected BucketingParams<Double> constructObject() {
        return new BucketingParams<>(12, 4.5, 5.9);
    }

    @Override
    protected Object readJson() throws IOException {
        return objectMapper.readValue(json(), new TypeReference<BucketingParams<Double>>() {});
    }

    @Override
    protected String json() {
        return "{\"targetNumberOfBuckets\": 12, \"min\": 4.5, \"max\": 5.9}";
    }

    @Override
    protected String toStringContent() {
        return "targetNumberOfBuckets";
    }
}
