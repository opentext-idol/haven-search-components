/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import java.io.Serializable;

@FunctionalInterface
public interface FieldsRequest extends Serializable {
    Integer getMaxValues();

    @FunctionalInterface
    interface Builder<F extends FieldsRequest> {
        F build();
    }
}
