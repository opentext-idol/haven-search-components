/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictionsBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.joda.time.DateTime;

import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodQueryRestrictionsImpl.HodQueryRestrictionsImplBuilder.class)
class HodQueryRestrictionsImpl implements HodQueryRestrictions {
    private static final long serialVersionUID = -6245470811532498784L;

    private final String queryText;
    private final String fieldText;
    @Singular
    private final List<ResourceIdentifier> databases;
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
    static class HodQueryRestrictionsImplBuilder implements HodQueryRestrictionsBuilder {
    }
}
