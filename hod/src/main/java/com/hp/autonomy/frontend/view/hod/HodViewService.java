/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.view.hod;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.error.HodErrorException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Service for viewing documents in Haven OnDemand text indexes.
 */
public interface HodViewService {

    /**
     * View the document with the given reference in the given index, writing the output to the given output stream.
     * @param reference The document reference
     * @param index The domain-qualified index
     * @param outputStream The output stream to write the viewed document to
     * @throws IOException
     * @throws HodErrorException
     */
    void viewDocument(String reference, ResourceIdentifier index, OutputStream outputStream) throws IOException, HodErrorException;

    /**
     * View a static content promotion, writing the output to the given output stream.
     * @param documentReference The reference of the search result created by the promotion
     * @param queryManipulationIndex The domain and name of the query manipulation index
     * @param outputStream The output stream to write the viewed document to
     * @throws IOException
     * @throws HodErrorException
     */
    void viewStaticContentPromotion(String documentReference, ResourceIdentifier queryManipulationIndex, OutputStream outputStream) throws IOException, HodErrorException;

}
