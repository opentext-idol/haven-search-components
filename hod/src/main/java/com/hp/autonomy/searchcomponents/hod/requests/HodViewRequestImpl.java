/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.hod.view.HodViewRequest;
import com.hp.autonomy.searchcomponents.hod.view.HodViewRequestBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * Default implementation of {@link HodViewRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodViewRequestImpl.HodViewRequestImplBuilder.class)
class HodViewRequestImpl implements HodViewRequest {
    private static final long serialVersionUID = -3421795905288242621L;

    private final String documentReference;
    private final ResourceName database;
    private final String highlightExpression;
    private final boolean original;

    @JsonPOJOBuilder(withPrefix = "")
    static class HodViewRequestImplBuilder implements HodViewRequestBuilder {
    }
}
