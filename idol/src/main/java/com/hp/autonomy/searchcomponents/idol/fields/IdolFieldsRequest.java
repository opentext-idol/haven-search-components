/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import lombok.Builder;
import lombok.Data;

/**
 * Options for interacting with Idol implementation of {@link FieldsService}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolFieldsRequest.IdolFieldsRequestBuilder.class)
public class IdolFieldsRequest implements FieldsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private final Integer maxValues;

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class IdolFieldsRequestBuilder implements FieldsRequest.FieldsRequestBuilder<IdolFieldsRequest> {
    }
}
