/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * Options for interacting with {@link DocumentsService#queryTextIndex(QueryRequest)}
 * Note that this object fulfills the lombok @Builder contract but does not use the annotation to avoid IntelliJ errors (https://github.com/mplushnikov/lombok-intellij-plugin/issues/127)
 *
 * @param <S> The type of the database identifier
 */
@Data
@JsonDeserialize(builder = QueryRequest.SearchRequestBuilder.class)
public class QueryRequest<S extends Serializable>
        implements AciSearchRequest<S>, RequestObject<QueryRequest<S>, QueryRequest.SearchRequestBuilder<S>> {
    private static final long serialVersionUID = -6338199353489914631L;

    private final boolean autoCorrect;
    private final QueryRestrictions<S> queryRestrictions;
    private final int start;
    private final int maxResults;
    private final String summary;
    private final Integer summaryCharacters;
    private final String sort;
    private final boolean highlight;
    private final String print;
    @Singular
    private final Collection<String> printFields;
    private final QueryType queryType;

    private QueryRequest(final SearchRequestBuilder<S> builder) {
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
        printFields = builder.printFields;
    }

    public enum QueryType {
        RAW, MODIFIED, PROMOTIONS
    }

    @Override
    public SearchRequestBuilder<S> toBuilder() {
        return new SearchRequestBuilder<>(this);
    }

    public static <S extends Serializable> SearchRequestBuilder<S> builder() {
        return new SearchRequestBuilder<>();
    }

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @Accessors(fluent = true)
    @Setter
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @JsonPOJOBuilder(withPrefix = "")
    public static class SearchRequestBuilder<S extends Serializable>
            implements RequestObject.RequestObjectBuilder<QueryRequest<S>, SearchRequestBuilder<S>> {
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
        private Collection<String> printFields = new HashSet<>();

        public SearchRequestBuilder(final QueryRequest<S> queryRequest) {
            queryRestrictions = queryRequest.queryRestrictions;
            start = queryRequest.start;
            maxResults = queryRequest.maxResults;
            summary = queryRequest.summary;
            summaryCharacters = queryRequest.summaryCharacters;
            sort = queryRequest.sort;
            highlight = queryRequest.highlight;
            autoCorrect = queryRequest.autoCorrect;
            print = queryRequest.print;
            queryType = queryRequest.queryType;
            printFields = queryRequest.printFields;
        }


        public SearchRequestBuilder<S> printField(final String printField) {
            printFields.add(printField);
            return this;
        }

        public SearchRequestBuilder<S> printFields(final Collection<? extends String> printFields) {
            this.printFields.addAll(printFields);
            return this;
        }

        public SearchRequestBuilder<S> clearPrintFields() {
            printFields.clear();
            return this;
        }

        @Override
        public QueryRequest<S> build() {
            return new QueryRequest<>(this);
        }
    }
}
