/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
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

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsService;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collection;

/**
 * Default implementation of {@link IdolFieldsService}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolFieldsRequestImpl.IdolFieldsRequestImplBuilder.class)
class IdolFieldsRequestImpl implements IdolFieldsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    @Singular
    private final Collection<FieldTypeParam> fieldTypes;
    private final Integer maxValues;

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolFieldsRequestImplBuilder implements IdolFieldsRequestBuilder {}
}
