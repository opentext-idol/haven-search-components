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
