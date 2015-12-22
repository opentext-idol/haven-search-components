/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.view.idol;

import lombok.Getter;

public class ViewServerErrorException extends RuntimeException {
    private static final long serialVersionUID = 6320193035187739418L;

    @Getter
    private final String reference;

    public ViewServerErrorException(final String reference, final Throwable cause) {
        super("HTTP error communicating with ViewServer", cause);

        this.reference = reference;
    }
}
