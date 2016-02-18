/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.configuration;

import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;

public interface HodSearchCapable extends HavenSearchCapable {
    QueryManipulationConfig getQueryManipulation();
}
