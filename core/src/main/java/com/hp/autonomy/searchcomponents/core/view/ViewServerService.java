/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public interface ViewServerService<S extends Serializable, E extends Exception> {

    /**
     * View the document with the given reference in the given index, writing the output to the given output stream.
     *
     * @param documentReference The document reference
     * @param database          The database or index containing the document
     * @param outputStream      The output stream to write the viewed document to
     * @throws E any error
     */
    void viewDocument(String documentReference, S database, OutputStream outputStream) throws E, IOException;
}
