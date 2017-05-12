/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import lombok.Data;

import java.io.Serializable;

@Data
public class BucketingParams<T extends Comparable<? super T> & Serializable> implements Serializable {
    private static final long serialVersionUID = 7148091304033066434L;

    private final int targetNumberOfBuckets;
    private final T min;
    private final T max;
}
