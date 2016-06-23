/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Data
@JsonDeserialize(builder = HodQueryRestrictions.Builder.class)
public class HodQueryRestrictions implements QueryRestrictions<ResourceIdentifier> {
    private static final long serialVersionUID = -6245470811532498784L;

    private final String queryText;
    private final String fieldText;
    private final List<ResourceIdentifier> databases;
    private final DateTime minDate;
    private final DateTime maxDate;
    private final Integer minScore;
    private final String languageType;
    private final boolean anyLanguage;

    // State tokens not yet supported in HoD, so leaving these out of the builder for now
    private final List<String> stateMatchId;
    private final List<String> stateDontMatchId;

    private HodQueryRestrictions(final Builder builder) {
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
    public static class Builder implements QueryRestrictions.Builder<HodQueryRestrictions, ResourceIdentifier> {
        private String queryText;
        private String fieldText;
        private List<ResourceIdentifier> databases = Collections.emptyList();
        private DateTime minDate;
        private DateTime maxDate;
        private Integer minScore;
        private String languageType;
        private boolean anyLanguage;
        private List<String> stateMatchId = Collections.emptyList();
        private List<String> stateDontMatchId = Collections.emptyList();

        @Override
        public HodQueryRestrictions build() {
            return new HodQueryRestrictions(this);
        }
    }
}
