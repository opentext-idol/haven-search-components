/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.regex.Pattern;

@Data
public class HodSearchResult implements SearchResult {
    private static final long serialVersionUID = -7386227266595690038L;

    private static final Pattern PATH_SEPARATOR_REGEX = Pattern.compile("/|\\\\");
    private static final String PROMOTION_PROPERTY = "promotion";

    private final String reference;
    private final String index;

    private final String title;
    private final String summary;
    private final Double weight;
    private final String contentType;
    private final String url;
    private final String offset;

    private final Map<String, FieldInfo<?>> fieldMap;

    private final DateTime date;

    private final PromotionCategory promotionCategory;

    private final String domain;

    private HodSearchResult(final Builder builder) {
        reference = builder.reference;
        index = builder.index;

        summary = builder.summary;
        contentType = builder.contentType;
        weight = builder.weight;
        url = builder.url;
        offset = builder.offset;
        fieldMap = builder.fieldMap;

        date = builder.date;

        promotionCategory = builder.promotionCategory == null ? PromotionCategory.NONE : builder.promotionCategory;

        if (builder.title == null && reference != null) {
            // If there is no title, assume the reference is a path and take the last part (the "file name")
            final String[] splitReference = PATH_SEPARATOR_REGEX.split(reference);
            final String lastPart = splitReference[splitReference.length - 1];

            title = StringUtils.isBlank(lastPart) || reference.endsWith("/") || reference.endsWith("\\") ? reference : lastPart;
        } else {
            title = builder.title;
        }

        domain = builder.domain;
    }

    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Builder {
        private String reference;
        private String index;

        private String title;
        private String summary;
        private Double weight;

        private String contentType;

        private String url;
        private String offset;

        private Map<String, FieldInfo<?>> fieldMap;

        private DateTime date;

        private PromotionCategory promotionCategory;

        private String domain;

        public Builder(final HodSearchResult document) {
            reference = document.reference;
            index = document.index;
            title = document.title;
            summary = document.summary;
            weight = document.weight;
            contentType = document.contentType;
            url = document.url;
            fieldMap = document.fieldMap;
            date = document.date;
            promotionCategory = document.promotionCategory;
            domain = document.domain;
        }

        public HodSearchResult build() {
            return new HodSearchResult(this);
        }
    }
}
