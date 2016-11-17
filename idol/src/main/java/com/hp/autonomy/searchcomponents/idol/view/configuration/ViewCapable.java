/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view.configuration;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Interface declaring that a configuration class contains ViewServer configuration
 */
@FunctionalInterface
public interface ViewCapable {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String VIEW_CONFIG_VALIDATOR_BEAN_NAME = "viewConfigValidator";

    /**
     * Returns configuration relating to interaction with Idol ViewServer
     *
     * @return ViewServer configuration
     */
    ViewConfig getViewConfig();
}
