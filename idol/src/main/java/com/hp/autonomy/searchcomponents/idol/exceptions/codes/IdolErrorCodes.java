/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions.codes;

import com.hp.autonomy.searchcomponents.idol.exceptions.IdolErrorCode;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum of error codes for different services, for use within {@link IdolService}
 */
public enum IdolErrorCodes {
    ANSWER_SERVER(AnswerServerErrorCode.values()),
    NO_OP(new IdolErrorCode[]{});

    private final IdolErrorCode<?>[] errorCodes;

    IdolErrorCodes(final IdolErrorCode<?>[] errorCodes) {
        this.errorCodes = errorCodes;
    }

    public Optional<IdolErrorCode<?>> forErrorId(final String errorId) {
        return Arrays.stream(errorCodes)
                .filter(value -> value.getErrorId().equals(errorId))
                .findFirst();
    }
}

