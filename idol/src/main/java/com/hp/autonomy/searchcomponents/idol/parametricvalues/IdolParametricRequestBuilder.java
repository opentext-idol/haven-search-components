/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;

/**
 * Builder for {@link IdolParametricRequest}
 */
public interface IdolParametricRequestBuilder extends ParametricRequestBuilder<IdolParametricRequest, IdolQueryRestrictions, IdolParametricRequestBuilder> {}
