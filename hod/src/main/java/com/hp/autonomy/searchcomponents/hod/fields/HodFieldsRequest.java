/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collection;

/**
 * Options for interacting with HoD implementation of {@link FieldsService}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodFieldsRequest.HodFieldsRequestBuilder.class)
public class HodFieldsRequest implements FieldsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    @Singular
    private Collection<ResourceIdentifier> databases;
    private Integer maxValues;

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class HodFieldsRequestBuilder implements FieldsRequest.FieldsRequestBuilder<HodFieldsRequest> {
    }
}
