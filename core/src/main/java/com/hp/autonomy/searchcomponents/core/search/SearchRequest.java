/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SearchRequest<S extends Serializable> implements AciSearchRequest<S> {
    private static final long serialVersionUID = -6338199353489914631L;

    protected QueryRestrictions<S> queryRestrictions;
    protected int start = DEFAULT_START;
    protected int maxResults = DEFAULT_MAX_RESULTS;
    protected String summary;
    protected Integer summaryCharacters;
    protected String sort;
    protected boolean highlight;
    protected boolean autoCorrect;
    protected String print = DEFAULT_PRINT;
    protected QueryType queryType = QueryType.MODIFIED;

    private SearchRequest(final Builder<S> builder) {
        queryRestrictions = builder.queryRestrictions;
        start = builder.start;
        maxResults = builder.maxResults;
        summary = builder.summary;
        summaryCharacters = builder.summaryCharacters;
        sort = builder.sort;
        highlight = builder.highlight;
        autoCorrect = builder.autoCorrect;
        print = builder.print;
        queryType = builder.queryType;
    }

    public enum QueryType {
        RAW, MODIFIED, PROMOTIONS
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Setter
    @Accessors(chain = true)
    public static class Builder<S extends Serializable> {
        private QueryRestrictions<S> queryRestrictions;
        private int start = DEFAULT_START;
        private int maxResults = DEFAULT_MAX_RESULTS;
        private String summary;
        private Integer summaryCharacters;
        private String sort;
        private boolean highlight;
        private boolean autoCorrect;
        private String print = DEFAULT_PRINT;
        private QueryType queryType = QueryType.MODIFIED;

        public SearchRequest<S> build() {
            return new SearchRequest<>(this);
        }
    }
}
