/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndex;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * Default implementation of {@link IdolGetContentRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolGetContentRequestImpl.IdolGetContentRequestImplBuilder.class)
class IdolGetContentRequestImpl implements IdolGetContentRequest {
    private static final long serialVersionUID = -6655229692839205599L;

    @Singular("indexAndReferences")
    private final Set<IdolGetContentRequestIndex> indexesAndReferences;
    private final PrintParam print;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolGetContentRequestImplBuilder implements IdolGetContentRequestBuilder {
    }
}
