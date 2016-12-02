/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
