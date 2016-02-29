/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class GetContentRequestIndex<S extends Serializable> implements Serializable {
    private static final long serialVersionUID = 6930992804864364983L;

    private final S index;
    private final Set<String> references;

    public GetContentRequestIndex(@JsonProperty("index") S index, @JsonProperty("references") Set<String> references) {
        this.index = index;
        this.references = references;
    }
}
