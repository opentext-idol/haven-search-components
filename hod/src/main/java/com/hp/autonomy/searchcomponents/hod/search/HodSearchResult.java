/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.PromotionType;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Data
@JsonDeserialize(builder = HodSearchResult.Builder.class)
public class HodSearchResult implements SearchResult {
    public static final List<String> ALL_FIELDS = Arrays.asList(
            CONTENT_TYPE_FIELD,
            URL_FIELD,
            OFFSET_FIELD,
            AUTHOR_FIELD,
            CATEGORY_FIELD,
            DATE_FIELD,
            DATE_CREATED_FIELD,
            CREATED_DATE_FIELD,
            DATE_MODIFIED_FIELD,
            MODIFIED_DATE_FIELD
    );

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

    private final List<String> authors;
    private final List<String> categories;

    private final DateTime date;
    private final DateTime dateCreated;
    private final DateTime dateModified;

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

        // LinkedList so we can guarantee Serializable
        authors = builder.authors == null ? Collections.<String>emptyList() : new LinkedList<>(builder.authors);
        categories = builder.categories == null ? Collections.<String>emptyList() : new LinkedList<>(builder.categories);

        date = builder.date;
        dateCreated = builder.dateCreated;
        dateModified = builder.dateModified;

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
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private String reference;
        private String index;

        private String title;
        private String summary;
        private Double weight;

        private String contentType;

        private String url;
        private String offset;

        @JsonProperty(AUTHOR_FIELD)
        private List<String> authors;

        @JsonProperty(CATEGORY_FIELD)
        private List<String> categories;

        private DateTime date;
        private DateTime dateCreated;
        private DateTime dateModified;

        private String qmsId;
        private String promotionName;

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
            offset = document.offset;
            authors = document.authors;
            categories = document.categories;
            date = document.date;
            dateCreated = document.dateCreated;
            dateModified = document.dateModified;
            promotionCategory = document.promotionCategory;
            domain = document.domain;
        }

        @JsonProperty(CONTENT_TYPE_FIELD)
        public Builder setContentType(final List<String> contentTypes) {
            if (contentTypes != null && !contentTypes.isEmpty()) {
                contentType = contentTypes.get(0);
            }

            return this;
        }

        @JsonProperty(URL_FIELD)
        public Builder setUrl(final List<String> urls) {
            if (urls != null && !urls.isEmpty()) {
                url = urls.get(0);
            }

            return this;
        }

        @JsonProperty(OFFSET_FIELD)
        public Builder setOffset(final List<String> offsets) {
            if (offsets != null && !offsets.isEmpty()) {
                offset = offsets.get(0);
            }

            return this;
        }

        @JsonProperty(DATE_FIELD)
        public Builder setDate(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                date = parsedDate;
            }

            return this;
        }

        @JsonProperty(DATE_CREATED_FIELD)
        public Builder setDateCreated(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateCreated = parsedDate;
            }

            return this;
        }

        @JsonProperty(CREATED_DATE_FIELD)
        public Builder setCreatedDate(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateCreated = parsedDate;
            }

            return this;
        }

        @JsonProperty(DATE_MODIFIED_FIELD)
        public Builder setDateModified(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateModified = parsedDate;
            }

            return this;
        }

        @JsonProperty(MODIFIED_DATE_FIELD)
        public Builder setModifiedDate(final List<String> dateStrings) {
            final DateTime parsedDate = parseDateList(dateStrings);

            if (parsedDate != null) {
                dateModified = parsedDate;
            }

            return this;
        }

        @JsonProperty(PROMOTION_PROPERTY)
        public Builder setPromotionCategory(final PromotionType promotionType) {
            switch (promotionType) {
                case DYNAMIC_PROMOTION:
                case STATIC_REFERENCE_PROMOTION:
                    promotionCategory = PromotionCategory.SPOTLIGHT;
                    break;
                case STATIC_CONTENT_PROMOTION:
                    promotionCategory = PromotionCategory.STATIC_CONTENT_PROMOTION;
                    break;
                case CARDINAL_PLACEMENT:
                    promotionCategory = PromotionCategory.CARDINAL_PLACEMENT;
                    break;
                case NONE:
                    promotionCategory = PromotionCategory.NONE;
                    break;
            }

            return this;
        }

        public HodSearchResult build() {
            return new HodSearchResult(this);
        }

        private DateTime parseDateList(final List<String> dateStrings) {
            if (dateStrings != null && !dateStrings.isEmpty()) {
                final DateTime parsedDate = parseDate(dateStrings.get(0));

                if (parsedDate != null) {
                    return parsedDate;
                }
            }

            return null;
        }

        // HOD handles date fields inconsistently; attempt to detect this here
        private DateTime parseDate(final String dateString) {
            DateTime result;

            try {
                // dateString is an ISO-8601 timestamp
                result = new DateTime(dateString);
            } catch (final IllegalArgumentException e) {
                // format is invalid, let's try a UNIX timestamp
                try {
                    result = new DateTime(Long.parseLong(dateString) * 1000L);
                } catch (final NumberFormatException e1) {
                    // date field is in a crazy unknown format, treat as if non-existent
                    result = null;
                }
            }

            return result;
        }
    }
}
