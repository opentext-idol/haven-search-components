/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.Processor;

import java.util.Collection;

public interface ViewServerService {

    <T> T viewDocument(String documentReference, Collection<String> indexes, Processor<T> processor) throws ViewDocumentNotFoundException, ViewNoReferenceFieldException, ReferenceFieldBlankException;
}
