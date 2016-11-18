/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.view.ViewRequest;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;
import lombok.Builder;
import lombok.Data;

/**
 * Options for interacting with Idol implementation of {@link ViewServerService}
 */
@SuppressWarnings("WeakerAccess")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodViewRequest.HodViewRequestBuilder.class)
public class HodViewRequest implements ViewRequest<ResourceIdentifier> {
    private static final long serialVersionUID = -3421795905288242621L;

    private final String documentReference;
    private final ResourceIdentifier database;
    private final String highlightExpression;

    @JsonPOJOBuilder(withPrefix = "")
    public static class HodViewRequestBuilder implements ViewRequestBuilder<HodViewRequest, ResourceIdentifier> {
    }
}
