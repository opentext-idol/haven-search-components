/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
