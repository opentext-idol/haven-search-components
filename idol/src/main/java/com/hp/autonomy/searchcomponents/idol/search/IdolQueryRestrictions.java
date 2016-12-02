/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;

import java.util.List;

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
