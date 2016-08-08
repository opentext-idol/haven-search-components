package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.Spelling;
import lombok.Getter;

@Getter
public class AutoCorrectException extends RuntimeException {
    private static final long serialVersionUID = 4599903956927532715L;

    private final Spelling spelling;

    public AutoCorrectException(final String msg, final Throwable cause, final Spelling spelling) {
        super(msg, cause);
        this.spelling = spelling;
    }
}
