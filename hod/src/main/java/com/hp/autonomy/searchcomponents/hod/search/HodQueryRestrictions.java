/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import lombok.*;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = HodQueryRestrictions.Builder.class)
public class HodQueryRestrictions implements QueryRestrictions<ResourceIdentifier> {
    private static final long serialVersionUID = -6245470811532498784L;

    private final String queryText;
    private final String fieldText;
    private final List<ResourceIdentifier> databases;
    private final DateTime minDate;
    private final DateTime maxDate;
    private final String languageType;
    private final boolean anyLanguage;

    // State tokens not yet supported in HoD, so leaving these out of the builder for now
    private final List<String> stateMatchId = Collections.emptyList();
    private final List<String> stateDontMatchId = Collections.emptyList();

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private String queryText;
        private String fieldText;
        private List<ResourceIdentifier> databases;
        private DateTime minDate;
        private DateTime maxDate;
        private String languageType;
        private boolean anyLanguage;

        public HodQueryRestrictions build() {
            return new HodQueryRestrictions(queryText, fieldText, databases, minDate, maxDate, languageType, anyLanguage);
        }
    }
}
