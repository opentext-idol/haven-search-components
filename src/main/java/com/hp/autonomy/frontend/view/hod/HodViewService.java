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

}
