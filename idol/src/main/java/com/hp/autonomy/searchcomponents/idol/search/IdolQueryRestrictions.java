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
import org.springframework.stereotype.Component;

import java.util.Collections;
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
    private final Integer minScore;
    private final String languageType;
    private final boolean anyLanguage;
    private final List<String> stateMatchId;
    private final List<String> stateDontMatchId;

    private IdolQueryRestrictions(final Builder builder) {
        queryText = builder.queryText;
        fieldText = builder.fieldText;
        databases = builder.databases;
        minDate = builder.minDate;
        maxDate = builder.maxDate;
        minScore = builder.minScore;
        languageType = builder.languageType;
        anyLanguage = builder.anyLanguage;
        stateMatchId = builder.stateMatchId;
        stateDontMatchId = builder.stateDontMatchId;
    }

    @Component
    @SuppressWarnings("FieldMayBeFinal")
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder implements QueryRestrictions.Builder<IdolQueryRestrictions, String> {
        private String queryText;
        private String fieldText;
        private List<String> databases = Collections.emptyList();
        private DateTime minDate;
        private DateTime maxDate;
        private Integer minScore;
        private String languageType;
        private boolean anyLanguage;
        private List<String> stateMatchId = Collections.emptyList();
        private List<String> stateDontMatchId = Collections.emptyList();

        @Override
        public IdolQueryRestrictions build() {
            return new IdolQueryRestrictions(this);
        }
    }
}
