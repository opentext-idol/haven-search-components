/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.ZonedDateTime;
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
    private final ZonedDateTime minDate;
    private final ZonedDateTime maxDate;
    private final Integer minScore;
    private final String languageType;
    private final boolean anyLanguage;
    @Singular
    private final List<String> stateMatchIds;
    @Singular
    private final List<String> stateDontMatchIds;

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolQueryRestrictionsImplBuilder implements IdolQueryRestrictionsBuilder {}
}
