/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

/**
 * Lookup interface to get a component name by host and port
 */
public interface IdolComponentLabelLookup {
    /**
     * Gets the name for an Idol component based upon its host name and port
     *
     * @param host component host name
     * @param port component port
     * @return friendly component name
     */
    String lookupComponentNameByHostAndPort(String host, int port);
}
