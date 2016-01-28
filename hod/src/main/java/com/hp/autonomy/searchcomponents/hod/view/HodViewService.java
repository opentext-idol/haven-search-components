/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.view;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Service for viewing documents in Haven OnDemand text indexes.
 */
public interface HodViewService extends ViewServerService<ResourceIdentifier, HodErrorException> {

    /**
     * View a static content promotion, writing the output to the given output stream.
     *
     * @param documentReference      The reference of the search result created by the promotion
     * @param queryManipulationIndex The domain and name of the query manipulation index
     * @param outputStream           The output stream to write the viewed document to
     * @throws IOException
     * @throws HodErrorException
     */
    void viewStaticContentPromotion(String documentReference, ResourceIdentifier queryManipulationIndex, OutputStream outputStream) throws IOException, HodErrorException;

}
