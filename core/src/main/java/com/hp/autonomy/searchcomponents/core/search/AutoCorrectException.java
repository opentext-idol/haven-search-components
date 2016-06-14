package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.Spelling;
import lombok.Getter;

@Getter
public class AutoCorrectException extends RuntimeException {
    private Spelling spelling;

    public AutoCorrectException(String msg, Throwable cause, Spelling spelling) {
        super(msg, cause);
        this.spelling = spelling;
    }
}
