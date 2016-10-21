/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.idol.responses.Hit;

@FunctionalInterface
public interface FieldsParser {
    void parseDocumentFields(Hit hit, IdolSearchResult.Builder searchResultBuilder);
}
