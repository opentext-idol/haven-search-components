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
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
public class SuggestRequest<S extends Serializable> implements AciSearchRequest<S> {
    private static final long serialVersionUID = -6338199353489914631L;

    protected String reference;
    protected QueryRestrictions<S> queryRestrictions;
    protected int start = DEFAULT_START;
    protected int maxResults = DEFAULT_MAX_RESULTS;
    protected String summary;
    protected Integer summaryCharacters;
    protected String sort;
    protected boolean highlight;
    protected String print = DEFAULT_PRINT;
    protected Collection<String> printFields = Collections.emptyList();


    private SuggestRequest(final Builder<S> builder) {
        reference = builder.reference;
        queryRestrictions = builder.queryRestrictions;
        start = builder.start;
        maxResults = builder.maxResults;
        summary = builder.summary;
        summaryCharacters = builder.summaryCharacters;
        sort = builder.sort;
        highlight = builder.highlight;
        print = builder.print;
        printFields = builder.printFields == null ? Collections.emptyList() : Collections.unmodifiableCollection(builder.printFields);
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Setter
    @Accessors(chain = true)
    public static class Builder<S extends Serializable> {
        private String reference;
        private QueryRestrictions<S> queryRestrictions;
        private int start = DEFAULT_START;
        private int maxResults = DEFAULT_MAX_RESULTS;
        private String summary;
        private Integer summaryCharacters;
        private String sort;
        private boolean highlight;
        private String print = DEFAULT_PRINT;
        private Collection<String> printFields = Collections.emptyList();

        public SuggestRequest<S> build() {
            return new SuggestRequest<>(this);
        }
    }
}
