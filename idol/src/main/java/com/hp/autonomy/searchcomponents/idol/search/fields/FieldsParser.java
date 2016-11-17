/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.idol.responses.Hit;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Parser for the document content returned in an Idol response
 */
@FunctionalInterface
public interface FieldsParser {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String FIELDS_PARSER_BEAN_NAME = "fieldsParser";

    /**
     * Parses the document content of an Idol response into a map of fields which is added to the builder object
     *
     * @param hit The document result containing content and metadata
     * @param searchResultBuilder The builder for generating a HavenSearch {@link SearchResult}
     */
    void parseDocumentFields(Hit hit, IdolSearchResult.IdolSearchResultBuilder searchResultBuilder);
}
