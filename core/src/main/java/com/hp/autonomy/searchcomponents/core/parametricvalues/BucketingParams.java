/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
