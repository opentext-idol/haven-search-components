package com.hp.autonomy.frontend.view.idol;

import lombok.Getter;

@Getter
public class ViewNoReferenceFieldException extends Exception {

    private final String reference;
    private final String referenceField;

    public ViewNoReferenceFieldException(final String reference, final String referenceField) {
        super("The document " + reference + " does not have a reference field: " + referenceField);
        this.reference = reference;
        this.referenceField = referenceField;
    }
}
