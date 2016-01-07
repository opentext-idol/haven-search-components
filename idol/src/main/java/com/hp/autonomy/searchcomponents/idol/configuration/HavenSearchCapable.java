/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.hp.autonomy.frontend.configuration.ServerConfig;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;

/**
 * Created by milleriv on 07/01/2016.
 */
public interface HavenSearchCapable extends ViewCapable {
    ServerConfig getContent();
}
