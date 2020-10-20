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
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndex;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * Default implementation of {@link IdolGetContentRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = IdolGetContentRequestImpl.IdolGetContentRequestImplBuilder.class)
class IdolGetContentRequestImpl implements IdolGetContentRequest {
    private static final long serialVersionUID = -6655229692839205599L;

    @Singular("indexAndReferences")
    private final Set<IdolGetContentRequestIndex> indexesAndReferences;
    private final PrintParam print;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    static class IdolGetContentRequestImplBuilder implements IdolGetContentRequestBuilder {
    }
}
