/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;

/**
 * Option for interacting with {@link IdolDocumentsService#getDocumentContent(GetContentRequest)}
 */
public interface IdolGetContentRequestIndex extends GetContentRequestIndex<String> {
    /**
     * {@inheritDoc}
     */
    @Override
    IdolGetContentRequestIndexBuilder toBuilder();
}
