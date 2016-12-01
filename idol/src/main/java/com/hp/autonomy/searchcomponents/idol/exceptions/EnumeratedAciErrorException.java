/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions;

import com.autonomy.aci.client.services.AciErrorException;
import lombok.Getter;

/**
 * An exception with a known AnswerServer error code
 */
@SuppressWarnings("WeakerAccess")
@Getter
public class EnumeratedAciErrorException extends RuntimeException {
    private static final long serialVersionUID = 3811886765556228103L;

    private final IdolErrorCode<?> errorCode;

    public EnumeratedAciErrorException(final String message, final AciErrorException cause, final IdolErrorCode<?> errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
