package com.hp.autonomy.frontend.view.idol;

import lombok.Getter;

public class ViewDocumentNotFoundException extends Exception {

    @Getter
    private final String reference;

    public ViewDocumentNotFoundException(final String reference) {
        super("The document " + reference + " could not be found");

        this.reference = reference;
    }


}