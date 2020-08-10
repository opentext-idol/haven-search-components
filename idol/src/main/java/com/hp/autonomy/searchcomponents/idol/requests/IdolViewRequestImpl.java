/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequestBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * Options for interacting with Idol implementation of {@link ViewServerService}
 */
@SuppressWarnings("WeakerAccess")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolViewRequestImpl.IdolViewRequestImplBuilder.class)
class IdolViewRequestImpl implements IdolViewRequest {
    private static final long serialVersionUID = 7368417933475466364L;

    private final String documentReference;
    private final String database;
    private final String highlightExpression;
    private final boolean original;

    @JsonPOJOBuilder(withPrefix = "")
    static class IdolViewRequestImplBuilder implements IdolViewRequestBuilder {
    }
}
