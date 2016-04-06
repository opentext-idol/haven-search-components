/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.searchcomponents.core.typeahead.GetSuggestionsFailedException;

import java.util.Set;

interface GetSuggestionsAciExecutor {
    <T> T executeAction(final AciService aciService, final Processor<T> processor, final Set<AciParameter> parameters) throws GetSuggestionsFailedException;
}
