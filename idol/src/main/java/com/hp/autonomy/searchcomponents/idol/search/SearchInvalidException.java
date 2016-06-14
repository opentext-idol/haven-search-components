package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.types.requests.Spelling;
import lombok.Getter;

@Getter
public class SearchInvalidException extends Exception {
    private Spelling spelling;

    public SearchInvalidException(String msg, Throwable cause, Spelling spelling) {
        super(msg, cause);
        this.spelling = spelling;
    }
}
