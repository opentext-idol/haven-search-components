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
        final ZonedDateTime min = ZonedDateTime.of(2017, 5, 10, 15, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.UTC));
        final ZonedDateTime max = ZonedDateTime.of(2017, 5, 10, 15, 40, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.UTC));
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
