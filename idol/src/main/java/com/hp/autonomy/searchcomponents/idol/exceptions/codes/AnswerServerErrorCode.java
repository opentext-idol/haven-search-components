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
import lombok.Getter;

/**
 * Known AnswerServer error codes
 */
@Getter
public enum AnswerServerErrorCode implements IdolErrorCode<AnswerServerErrorCode> {
    STOP_WORDS("ANSWERSERVERGETRESOURCES512"),// Query is made up of stopwords only, or incorrectly uses of operators
    BOOLEAN_OPERATOR("ANSWERSERVERGETRESOURCES509");// Terminating boolean operator

    private final String errorId;

    AnswerServerErrorCode(final String errorId) {
        this.errorId = errorId;
    }


    @Override
    public AnswerServerErrorCode getEnum() {
        return this;
    }
}
