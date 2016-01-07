/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import java.io.Serializable;
import java.util.Set;

public interface ParametricRequest<S extends Serializable> extends Serializable {
    Set<S> getDatabases();

    Set<String> getFieldNames();

    String getQueryText();

    String getFieldText();
}
