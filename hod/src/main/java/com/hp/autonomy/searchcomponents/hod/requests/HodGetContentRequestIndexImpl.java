/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodDocumentsService;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndex;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndexBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * Option for interacting with {@link HodDocumentsService#getDocumentContent(GetContentRequest)}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodGetContentRequestIndexImpl.HodGetContentRequestIndexImplBuilder.class)
class HodGetContentRequestIndexImpl implements HodGetContentRequestIndex {
    private static final long serialVersionUID = 6930992804864364983L;

    private final ResourceName index;
    @Singular
    private final Set<String> references;

    @JsonPOJOBuilder(withPrefix = "")
    static class HodGetContentRequestIndexImplBuilder implements HodGetContentRequestIndexBuilder {}
}
