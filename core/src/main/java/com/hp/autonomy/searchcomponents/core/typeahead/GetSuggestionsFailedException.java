/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.typeahead;

public class GetSuggestionsFailedException extends Exception {
    private static final long serialVersionUID = -8407169773270833099L;

    public GetSuggestionsFailedException(final Exception cause) {
        super("Failed to get suggestions", cause);
    }
}
