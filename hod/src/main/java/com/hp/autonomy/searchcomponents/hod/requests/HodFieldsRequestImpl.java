/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.hod.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequestBuilder;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
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
    private final Collection<FieldTypeParam> fieldTypes;
    @Singular
    private Collection<ResourceName> databases;
    private Integer maxValues;

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    static class HodFieldsRequestImplBuilder implements HodFieldsRequestBuilder {
    }
}
