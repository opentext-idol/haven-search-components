/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Data
public class IdolSearchResult implements SearchResult {
    public static final String QMS_ID_FIELD = "qmsid";
    public static final String INJECTED_PROMOTION_FIELD = "injectedpromotion";
    public static final String THUMBNAIL = "HPSW_APP_NOINDEX_PREVIEWTHBNAILBASE64";

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
            MODIFIED_DATE_FIELD,
            QMS_ID_FIELD,
            INJECTED_PROMOTION_FIELD,
            THUMBNAIL
    );

    private static final long serialVersionUID = 4767058593555545628L;

    private static final Pattern PATH_SEPARATOR_REGEX = Pattern.compile("/|\\\\");

    private final String reference;
    private final String index;

    private final String title;
    private final String summary;
    private final Double weight;
    private final String contentType;
    private final String url;
    private final String offset;
    private final String thumbnail;

    private final List<String> authors;
    private final List<String> categories;

    private final DateTime date;
    private final DateTime dateCreated;
    private final DateTime dateModified;

    private final String qmsId;
    private final String promotionName;
    private final PromotionCategory promotionCategory;

    private IdolSearchResult(final Builder builder) {
        reference = builder.reference;
        index = builder.index;

        summary = builder.summary;
        contentType = builder.contentType;
        weight = builder.weight;
        url = builder.url;
        offset = builder.offset;
        thumbnail = builder.thumbnail;

        // LinkedList so we can guarantee Serializable
        authors = builder.authors == null ? Collections.<String>emptyList() : new LinkedList<>(builder.authors);
        categories = builder.categories == null ? Collections.<String>emptyList() : new LinkedList<>(builder.categories);

        date = builder.date;
        dateCreated = builder.dateCreated;
        dateModified = builder.dateModified;

        qmsId = builder.qmsId;
        promotionName = builder.promotionName;
        promotionCategory = builder.promotionCategory == null ? PromotionCategory.NONE : builder.promotionCategory;

        if (builder.title == null && reference != null) {
            // If there is no title, assume the reference is a path and take the last part (the "file name")
            final String[] splitReference = PATH_SEPARATOR_REGEX.split(reference);
            final String lastPart = splitReference[splitReference.length - 1];
            title = StringUtils.isBlank(lastPart) || reference.endsWith("/") || reference.endsWith("\\") ? reference : lastPart;
        } else {
            title = builder.title;
        }
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
        private String thumbnail;

        private List<String> authors;

        private List<String> categories;

        private DateTime date;
        private DateTime dateCreated;
        private DateTime dateModified;

        private String qmsId;
        private String promotionName;

        private PromotionCategory promotionCategory;

        public Builder(final IdolSearchResult document) {
            reference = document.reference;
            index = document.index;
            title = document.title;
            summary = document.summary;
            weight = document.weight;
            contentType = document.contentType;
            url = document.url;
            offset = document.offset;
            thumbnail = document.thumbnail;
            authors = document.authors;
            categories = document.categories;
            date = document.date;
            dateCreated = document.dateCreated;
            dateModified = document.dateModified;
            qmsId = document.qmsId;
            promotionName = document.promotionName;
            promotionCategory = document.promotionCategory;
        }

        @SuppressWarnings("UseOfObsoleteDateTimeApi")
        public Builder setDate(final Date date) {
            if (date != null) {
                this.date = new DateTime(date);
            }

            return this;
        }

        public IdolSearchResult build() {
            return new IdolSearchResult(this);
        }
    }
}
