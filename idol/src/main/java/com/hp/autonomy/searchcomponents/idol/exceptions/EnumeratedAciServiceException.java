/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions;

import lombok.Getter;

/**
 * Any service level error with a defined enum type (used for performing localization of the error response in the client)
 */
@SuppressWarnings("WeakerAccess")
@Getter
public class EnumeratedAciServiceException extends RuntimeException {
    private static final long serialVersionUID = -5873344336995393810L;

    private final AciServiceError<?> errorCode;

    public EnumeratedAciServiceException(final String message, final AciServiceError<?> errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
