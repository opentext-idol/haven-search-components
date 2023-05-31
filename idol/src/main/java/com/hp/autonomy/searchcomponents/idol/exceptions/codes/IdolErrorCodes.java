/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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

