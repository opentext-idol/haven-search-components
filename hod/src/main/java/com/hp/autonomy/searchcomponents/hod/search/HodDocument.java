/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.PromotionType;
import com.hp.autonomy.searchcomponents.core.search.HavenDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = HodDocument.Builder.class)
public class HodDocument extends HavenDocument {
    private static final long serialVersionUID = -7386227266595690038L;

    private final String domain;
    private final PromotionType promotionType;

    public HodDocument(final Builder builder) {
        super(builder);
        domain = builder.domain;
        promotionType = builder.promotionType == null ? PromotionType.NONE : builder.promotionType;
    }

    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder extends HavenDocument.Builder {
        private String domain;

        @JsonProperty("promotion")
        private PromotionType promotionType;

        public Builder(final HodDocument document) {
            super(document);
            domain = document.domain;
            promotionType = document.promotionType;
        }

        @Override
        public HodDocument build() {
            return new HodDocument(this);
        }
    }
}
