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
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodDocumentsService;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndex;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * Default implementation of {@link HodGetContentRequest}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodGetContentRequestImpl.HodGetContentRequestImplBuilder.class)
class HodGetContentRequestImpl implements HodGetContentRequest {
    private static final long serialVersionUID = -6655229692839205599L;

    @Singular("indexAndReferences")
    private final Set<HodGetContentRequestIndex> indexesAndReferences;
    private final Print print;

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    static class HodGetContentRequestImplBuilder implements HodGetContentRequestBuilder {
    }
}
