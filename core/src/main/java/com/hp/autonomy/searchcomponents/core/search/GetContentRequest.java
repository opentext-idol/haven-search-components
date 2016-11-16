/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Options for interacting with {@link DocumentsService#getDocumentContent(GetContentRequest)}
 * Note that this object fulfills the lombok @Builder contract but does not use the annotation to avoid IntelliJ errors (https://github.com/mplushnikov/lombok-intellij-plugin/issues/127)
 *
 * @param <S> The type of the database identifier
 */
@Data
// We should remove the @AllArgsConstructor and add @JsonDeserialize if Jackson fix the bug which prevents the type parameter from being understood
@AllArgsConstructor
//@JsonDeserialize(builder = GetContentRequest.GetContentRequestBuilder.class)
public class GetContentRequest<S extends Serializable>
        implements RequestObject<GetContentRequest<S>, GetContentRequest.GetContentRequestBuilder<S>> {
    private static final long serialVersionUID = -6655229692839205599L;

    @JsonDeserialize(contentAs = GetContentRequestIndex.class)
    @JsonProperty("indexesAndReferences")
    @Singular("indexAndReferences")
    private final Set<GetContentRequestIndex<S>> indexesAndReferences;
    private final String print;

    private GetContentRequest(final GetContentRequestBuilder<S> builder) {
        indexesAndReferences = builder.indexesAndReferences;
        print = builder.print;
    }

    @Override
    public GetContentRequestBuilder<S> toBuilder() {
        return new GetContentRequestBuilder<>(this);
    }

    public static <S extends Serializable> GetContentRequestBuilder<S> builder() {
        return new GetContentRequestBuilder<>();
    }

    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    @Accessors(fluent = true)
    @Setter
    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "")
    public static class GetContentRequestBuilder<S extends Serializable>
            implements RequestObject.RequestObjectBuilder<GetContentRequest<S>, GetContentRequest.GetContentRequestBuilder<S>> {
        private Set<GetContentRequestIndex<S>> indexesAndReferences = new HashSet<>();
        private String print;

        private GetContentRequestBuilder(final GetContentRequest<S> request) {
            indexesAndReferences = request.indexesAndReferences;
            print = request.print;
        }

        public GetContentRequestBuilder<S> indexAndReferences(final GetContentRequestIndex<S> indexAndReferences) {
            indexesAndReferences.add(indexAndReferences);
            return this;
        }

        public GetContentRequestBuilder<S> indexesAndReferences(final Collection<? extends GetContentRequestIndex<S>> indexesAndReferences) {
            this.indexesAndReferences.addAll(indexesAndReferences);
            return this;
        }

        public GetContentRequestBuilder<S> clearIndexesAndReferences() {
            indexesAndReferences.clear();
            return this;
        }

        @Override
        public GetContentRequest<S> build() {
            return new GetContentRequest<>(this);
        }
    }
}
