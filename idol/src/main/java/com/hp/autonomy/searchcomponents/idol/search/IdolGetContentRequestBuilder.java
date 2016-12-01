/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.GetContentRequestBuilder;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;

/**
 * Builder for {@link IdolGetContentRequest}
 */
public interface IdolGetContentRequestBuilder extends GetContentRequestBuilder<IdolGetContentRequest, IdolGetContentRequestIndex, IdolGetContentRequestBuilder> {
    /**
     * Sets what to display in the document result output
     *
     * @param print What to display in the document result output
     * @return the builder (for chaining)
     */
    IdolGetContentRequestBuilder print(PrintParam print);
}
