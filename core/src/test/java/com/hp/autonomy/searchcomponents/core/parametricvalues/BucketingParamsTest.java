package com.hp.autonomy.searchcomponents.core.parametricvalues;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BucketingParamsTest {
    @Test
    public void equals() {
        final BucketingParams bucketingParams1 = new BucketingParams(12, null, null);
        final BucketingParams bucketingParams2 = new BucketingParams(12, null, null);
        assertThat(bucketingParams1.equals(bucketingParams2), is(true));
    }
}
