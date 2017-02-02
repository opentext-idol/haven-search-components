/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.view;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;

/**
 * HoD extension to {@link ViewServerService}
 */
@SuppressWarnings("WeakerAccess")
public interface HodViewServerService extends ViewServerService<HodViewRequest, ResourceName, HodErrorException> {
}
