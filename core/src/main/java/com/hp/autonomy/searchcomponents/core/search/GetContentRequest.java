/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class GetContentRequest<S extends Serializable> implements Serializable {
    private static final long serialVersionUID = -6655229692839205599L;

    private final Set<GetContentRequestIndex<S>> indexesAndReferences;

    public GetContentRequest(
            @JsonDeserialize(contentAs = GetContentRequestIndex.class)
            @JsonProperty("indexesAndReferences")
            final Set<GetContentRequestIndex<S>> indexesAndReferences
    ) {
        this.indexesAndReferences = new LinkedHashSet<>(indexesAndReferences);
    }
}
