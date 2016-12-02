/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestBuilder;

/**
 * Builder for {@link HodGetContentRequest}
 */
public interface HodGetContentRequestBuilder extends GetContentRequestBuilder<HodGetContentRequest, HodGetContentRequestIndex, HodGetContentRequestBuilder> {
    /**
     * Sets what to display in the document result output
     *
     * @param print What to display in the document result output
     * @return the builder (for chaining)
     */
    HodGetContentRequestBuilder print(Print print);
}
