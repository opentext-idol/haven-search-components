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
