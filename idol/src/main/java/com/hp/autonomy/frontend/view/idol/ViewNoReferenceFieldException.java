/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.view.idol;

import lombok.Getter;

@Getter
public class ViewNoReferenceFieldException extends RuntimeException {
    private static final long serialVersionUID = 7127764158103167360L;

    private final String reference;
    private final String referenceField;

    public ViewNoReferenceFieldException(final String reference, final String referenceField) {
        super("The document " + reference + " does not have a reference field: " + referenceField);
        this.reference = reference;
        this.referenceField = referenceField;
    }
}
