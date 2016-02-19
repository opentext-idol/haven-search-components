/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("InstanceVariableOfConcreteClass")
@Data
public class IdolSearchResult implements SearchResult {
    private static final long serialVersionUID = 4767058593555545628L;

    private static final Pattern PATH_SEPARATOR_REGEX = Pattern.compile("/|\\\\");

    private final String reference;
    private final String index;

    private final String title;
    private final String summary;
    private final Double weight;

    private final Map<String, FieldInfo<?>> fieldMap;

    private final DateTime date;

    private final String qmsId;
    private final String promotionName;
    private final PromotionCategory promotionCategory;

    private IdolSearchResult(final Builder builder) {
        reference = builder.reference;
        index = builder.index;

        summary = builder.summary;
        weight = builder.weight;

        date = builder.date;
        fieldMap = builder.fieldMap;

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

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Builder {
        private String reference;
        private String index;

        private String title;
        private String summary;
        private Double weight;

        private Map<String, FieldInfo<?>> fieldMap;

        private DateTime date;

        private String qmsId;
        private String promotionName;

        private PromotionCategory promotionCategory;

        public Builder(final IdolSearchResult document) {
            reference = document.reference;
            index = document.index;
            title = document.title;
            summary = document.summary;
            weight = document.weight;
            fieldMap = document.fieldMap;
            date = document.date;
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
