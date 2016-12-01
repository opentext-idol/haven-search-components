/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;

/**
 * Options for interacting with {@link HodDocumentsService#getDocumentContent(GetContentRequest)}
 */
public interface HodGetContentRequest extends GetContentRequest<HodGetContentRequestIndex> {
    /**
     * What to display in the document result output
     *
     * @return What to display in the document result output
     */
    Print getPrint();

    /**
     * {@inheritDoc}
     */
    @Override
    HodGetContentRequestBuilder toBuilder();
}
