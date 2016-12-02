/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.joda.time.DateTime;

import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolQueryRestrictionsImpl.IdolQueryRestrictionsImplBuilder.class)
class IdolQueryRestrictionsImpl implements IdolQueryRestrictions {
    private static final long serialVersionUID = 4719575144105438353L;

    private final String queryText;
    private final String fieldText;
    @Singular
    private final List<String> databases;
    private final DateTime minDate;
    private final DateTime maxDate;
    private final Integer minScore;
    private final String languageType;
    private final boolean anyLanguage;
    @Singular
    private final List<String> stateMatchIds;
    @Singular
    private final List<String> stateDontMatchIds;

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolQueryRestrictionsImplBuilder implements IdolQueryRestrictionsBuilder {
    }
}
