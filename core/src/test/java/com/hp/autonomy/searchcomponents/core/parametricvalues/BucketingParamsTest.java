package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.searchcomponents.core.requests.SimpleRequestObjectTest;

public class BucketingParamsTest extends SimpleRequestObjectTest<BucketingParams> {
    @Override
    protected BucketingParams constructObject() {
        return new BucketingParams(12, 4.5, 5.9);
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
