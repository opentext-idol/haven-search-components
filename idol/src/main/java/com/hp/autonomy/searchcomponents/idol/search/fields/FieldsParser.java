/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.opentext.idol.types.responses.Hit;
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
