package com.hp.autonomy.frontend.view.idol;

import lombok.Getter;

public class ViewServerErrorException extends RuntimeException {

    @Getter
    private final int statusCode;
    @Getter
    private final String reference;

    public ViewServerErrorException(final int statusCode, final String reference) {
        super("ViewServer returned an HTTP status code " + statusCode);

        this.statusCode = statusCode;
        this.reference = reference;
    }
}
