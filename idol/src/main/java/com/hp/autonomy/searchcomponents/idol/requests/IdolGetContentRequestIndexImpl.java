/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolDocumentsService;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndex;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndexBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * Option for interacting with {@link IdolDocumentsService#getDocumentContent(GetContentRequest)}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolGetContentRequestIndexImpl.IdolGetContentRequestIndexImplBuilder.class)
class IdolGetContentRequestIndexImpl implements IdolGetContentRequestIndex {
    private static final long serialVersionUID = 6930992804864364983L;

    private final String index;
    @Singular
    private final Set<String> references;

    @JsonPOJOBuilder(withPrefix = "")
    static class IdolGetContentRequestIndexImplBuilder implements IdolGetContentRequestIndexBuilder {
    }
}
