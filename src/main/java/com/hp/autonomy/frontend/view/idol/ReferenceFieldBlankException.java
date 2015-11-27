package com.hp.autonomy.frontend.view.idol;

public class ReferenceFieldBlankException extends Exception {

    public ReferenceFieldBlankException() {
        super("Your ViewServer settings are not correctly configured.");
    }

}