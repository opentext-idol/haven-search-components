/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictionsBuilder;

/**
 * Builder for {@link HodQueryRestrictions}
 */
public interface HodQueryRestrictionsBuilder extends QueryRestrictionsBuilder<HodQueryRestrictions, ResourceName, HodQueryRestrictionsBuilder> {}
