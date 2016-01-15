/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.Documents;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface DocumentsService<S extends Serializable, D extends SearchResult, E extends Exception> {
    String HIGHLIGHT_START_TAG = "<HavenSearch-QueryText-Placeholder>";
    String HIGHLIGHT_END_TAG = "</HavenSearch-QueryText-Placeholder>";

    Documents<D> queryTextIndex(final SearchRequest<S> searchRequest) throws E;

    Documents<D> queryTextIndexForPromotions(final SearchRequest<S> searchRequest) throws E;

    List<D> findSimilar(Set<S> indexes, String reference) throws E;

}
