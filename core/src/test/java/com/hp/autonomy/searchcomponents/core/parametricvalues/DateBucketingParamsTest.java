/*
 * Copyright 2017 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hp.autonomy.searchcomponents.core.requests.SimpleRequestObjectTest;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateBucketingParamsTest extends SimpleRequestObjectTest<BucketingParams<ZonedDateTime>> {
    @Override
    protected BucketingParams<ZonedDateTime> constructObject() {
        final ZonedDateTime min = ZonedDateTime.of(2017, 5, 10, 15, 0, 0, 0, ZoneOffset.ofTotalSeconds(0));
        final ZonedDateTime max = ZonedDateTime.of(2017, 5, 10, 15, 40, 0, 0, ZoneOffset.ofTotalSeconds(0));
        return new BucketingParams<>(12, min, max);
    }

    @Override
    protected Object readJson() throws IOException {
        return objectMapper.readValue(json(), new TypeReference<BucketingParams<ZonedDateTime>>() {
        });
    }

    @Override
    protected String json() {
        return "{\"targetNumberOfBuckets\": 12, \"min\": \"2017-05-10T15:00:00Z\", \"max\": \"2017-05-10T15:40:00Z\"}";
    }

    @Override
    protected String toStringContent() {
        return "targetNumberOfBuckets";
    }
}
