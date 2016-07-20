package com.hp.autonomy.searchcomponents.core.parametricvalues;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BucketingParamsTest {
    @Test
    public void equals() {
        final BucketingParams bucketingParams1 = new BucketingParams(12, 4.5, 5.9);
        final BucketingParams bucketingParams2 = new BucketingParams(12, 4.5, 5.9);
        assertThat(bucketingParams1.equals(bucketingParams2), is(true));
    }

    @Test
    public void notEqual() {
        final BucketingParams bucketingParams1 = new BucketingParams(12, 4.5, 5.9);
        final BucketingParams bucketingParams2 = new BucketingParams(12, 4.5, 7.5);
        assertThat(bucketingParams1.equals(bucketingParams2), is(false));
    }
}
