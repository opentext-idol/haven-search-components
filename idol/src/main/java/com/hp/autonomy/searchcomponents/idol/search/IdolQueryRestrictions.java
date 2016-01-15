/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = IdolQueryRestrictions.Builder.class)
public class IdolQueryRestrictions implements QueryRestrictions<String> {
    private static final long serialVersionUID = 4719575144105438353L;

    private final String queryText;
    private final String fieldText;
    private final List<String> databases;
    private final DateTime minDate;
    private final DateTime maxDate;
    private final String languageType;
    private final boolean anyLanguage;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private String queryText;
        private String fieldText;
        private List<String> databases;
        private DateTime minDate;
        private DateTime maxDate;
        private String languageType;
        private boolean anyLanguage;

        public IdolQueryRestrictions build() {
            return new IdolQueryRestrictions(queryText, fieldText, databases, minDate, maxDate, languageType, anyLanguage);
        }
    }
}
