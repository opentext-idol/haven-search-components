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
