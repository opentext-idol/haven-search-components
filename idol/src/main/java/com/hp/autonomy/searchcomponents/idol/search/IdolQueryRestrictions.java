/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;

/**
 * Filter restrictions to apply to queries against Idol
 */
public interface IdolQueryRestrictions extends QueryRestrictions<String> {
    /**
     * {@inheritDoc}
     */
    @Override
    IdolQueryRestrictionsBuilder toBuilder();
}
