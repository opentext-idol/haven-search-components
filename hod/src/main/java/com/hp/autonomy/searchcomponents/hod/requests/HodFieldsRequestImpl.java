/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequestBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collection;

/**
 * Default implementation of {@link HodFieldsRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodFieldsRequestImpl.HodFieldsRequestImplBuilder.class)
class HodFieldsRequestImpl implements HodFieldsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    @Singular
    private Collection<ResourceIdentifier> databases;
    private Integer maxValues;

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    static class HodFieldsRequestImplBuilder implements HodFieldsRequestBuilder {
    }
}
