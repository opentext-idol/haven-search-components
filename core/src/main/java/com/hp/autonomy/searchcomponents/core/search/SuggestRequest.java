/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * Options for interacting with {@link DocumentsService#findSimilar(SuggestRequest)}
 * Note that this object fulfills the lombok @Builder contract but does not use the annotation to avoid IntelliJ errors (https://github.com/mplushnikov/lombok-intellij-plugin/issues/127)
 *
 * @param <S> The type of the database identifier
 */
@Data
@JsonDeserialize(builder = SuggestRequest.SuggestRequestBuilder.class)
public class SuggestRequest<S extends Serializable>
        implements AciSearchRequest<S>, RequestObject<SuggestRequest<S>, SuggestRequest.SuggestRequestBuilder<S>> {
    private static final long serialVersionUID = -6338199353489914631L;

    private final String reference;
    private final QueryRestrictions<S> queryRestrictions;
    private final int start;
    private final int maxResults;
    private final String summary;
    private final Integer summaryCharacters;
    private final String sort;
    private final boolean highlight;
    private final String print;
    private final Collection<String> printFields;

    private SuggestRequest(final SuggestRequestBuilder<S> builder) {
        reference = builder.reference;
        queryRestrictions = builder.queryRestrictions;
        start = builder.start;
        maxResults = builder.maxResults;
        summary = builder.summary;
        summaryCharacters = builder.summaryCharacters;
        sort = builder.sort;
        highlight = builder.highlight;
        print = builder.print;
        printFields = builder.printFields;
    }

    @Override
    public SuggestRequestBuilder<S> toBuilder() {
        return new SuggestRequestBuilder<>(this);
    }

    public static <S extends Serializable> SuggestRequestBuilder<S> builder() {
        return new SuggestRequestBuilder<>();
    }

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @Accessors(fluent = true)
    @Setter
    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "")
    public static class SuggestRequestBuilder<S extends Serializable>
            implements RequestObject.RequestObjectBuilder<SuggestRequest<S>, SuggestRequestBuilder<S>> {
        private String reference;
        private QueryRestrictions<S> queryRestrictions;
        private int start = DEFAULT_START;
        private int maxResults = DEFAULT_MAX_RESULTS;
        private String summary;
        private Integer summaryCharacters;
        private String sort;
        private boolean highlight;
        private String print = DEFAULT_PRINT;
        private Collection<String> printFields = new HashSet<>();

        public SuggestRequestBuilder(final SuggestRequest<S> suggestRequest) {
            reference = suggestRequest.reference;
            queryRestrictions = suggestRequest.queryRestrictions;
            start = suggestRequest.start;
            maxResults = suggestRequest.maxResults;
            summary = suggestRequest.summary;
            summaryCharacters = suggestRequest.summaryCharacters;
            sort = suggestRequest.sort;
            highlight = suggestRequest.highlight;
            print = suggestRequest.print;
            printFields = suggestRequest.printFields;
        }


        public SuggestRequestBuilder<S> printField(final String printField) {
            printFields.add(printField);
            return this;
        }

        public SuggestRequestBuilder<S> printFields(final Collection<? extends String> printFields) {
            this.printFields.addAll(printFields);
            return this;
        }

        public SuggestRequestBuilder<S> clearPrintFields() {
            printFields.clear();
            return this;
        }

        @Override
        public SuggestRequest<S> build() {
            return new SuggestRequest<>(this);
        }
    }
}
