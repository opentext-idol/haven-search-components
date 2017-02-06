/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.caching;

import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;

import java.util.Map;

/**
 * Identifiers to use for caching of services
 */
public interface CacheNames {
    /**
     * Cache identifier for {@link FieldsService#getFields(FieldsRequest, FieldTypeParam...)}
     */
    String FIELDS = "fields";

    /**
     * Cache identifier for {@link ParametricValuesService#getParametricValues(ParametricRequest)}
     */
    String PARAMETRIC_VALUES = "parametric-values";

    /**
     * Cache identifier for {@link ParametricValuesService#getNumericParametricValuesInBuckets(ParametricRequest, Map)}
     */
    String PARAMETRIC_VALUES_IN_BUCKETS = "parametric-values-in-buckets";

    /**
     * Cache identifier for {@link DocumentsService#queryTextIndex(QueryRequest)}
     */
    String GET_DOCUMENT_CONTENT = "get-content";

    /**
     * Cache identifier for {@link RelatedConceptsService#findRelatedConcepts(RelatedConceptsRequest)}
     */
    String RELATED_CONCEPTS = "related-concepts";

    /**
     * Cache identifier for {@link TypeAheadService#getSuggestions(String)}
     */
    String TYPE_AHEAD = "type-ahead";
}
