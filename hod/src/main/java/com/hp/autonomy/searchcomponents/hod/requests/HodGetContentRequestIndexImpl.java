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
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodDocumentsService;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndex;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndexBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * Option for interacting with {@link HodDocumentsService#getDocumentContent(GetContentRequest)}
 */
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = HodGetContentRequestIndexImpl.HodGetContentRequestIndexImplBuilder.class)
class HodGetContentRequestIndexImpl implements HodGetContentRequestIndex {
    private static final long serialVersionUID = 6930992804864364983L;

    private final ResourceName index;
    @Singular
    private final Set<String> references;

    @JsonPOJOBuilder(withPrefix = "")
    static class HodGetContentRequestIndexImplBuilder implements HodGetContentRequestIndexBuilder {}
}
