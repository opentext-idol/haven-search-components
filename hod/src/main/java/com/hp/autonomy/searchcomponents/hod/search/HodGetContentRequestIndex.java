/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;

/**
 * Option for interacting with {@link HodDocumentsService#getDocumentContent(GetContentRequest)}
 */
public interface HodGetContentRequestIndex extends GetContentRequestIndex<ResourceName> {
    /**
     * {@inheritDoc}
     */
    @Override
    HodGetContentRequestIndexBuilder toBuilder();
}
