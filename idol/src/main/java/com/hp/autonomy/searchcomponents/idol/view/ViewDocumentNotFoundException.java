/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import lombok.Getter;

public class ViewDocumentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 2505063806464536375L;
    @Getter
    private final String reference;

    public ViewDocumentNotFoundException(final String reference) {
        this(reference, null);
    }

    public ViewDocumentNotFoundException(final String reference, final Exception cause) {
        super("The document " + reference + " could not be found", cause);
        this.reference = reference;
    }
}