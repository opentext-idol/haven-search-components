/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.system;

import com.autonomy.aci.client.transport.AciServerDetails;

import java.util.Collection;

/**
 * Performs general operations on answer server
 */
public interface AnswerServerSystemService {
    /**
     * Returns the list of configured system names using configured server details
     *
     * @return the list of configured system names
     */
    Collection<String> getSystemNames();

    /**
     * Returns the list of configured system names using specified server details
     *
     * @param aciServerDetails Server details
     * @return the list of configured system names
     */
    Collection<String> getSystemNames(AciServerDetails aciServerDetails);
}
