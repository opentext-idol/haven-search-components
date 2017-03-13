/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.search.DocumentTitleResolver;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings({"InstanceVariableOfConcreteClass", "MismatchedQueryAndUpdateOfCollection"})
@Data
@Builder(toBuilder = true)
public class IdolSearchResult implements SearchResult {
    private static final long serialVersionUID = 4767058593555545628L;

    private static final Pattern PATH_SEPARATOR_REGEX = Pattern.compile("/|\\\\");

    private final String reference;
    private final String index;

    private final String title;
    private final String summary;
    private final Double weight;

    @Singular("fieldEntry")
    private final Map<String, FieldInfo<?>> fieldMap;

    private final DateTime date;

    private final String qmsId;
    private final String promotionName;
    private final PromotionCategory promotionCategory;

    private IdolSearchResult(final IdolSearchResultBuilder builder) {
        reference = builder.reference;
        index = builder.index;

        summary = builder.summary;
        weight = builder.weight;

        date = builder.date;
        fieldMap = new HashMap<>();
        if (builder.fieldMap$key != null && builder.fieldMap$value != null) {
            for (int i = 0; i < builder.fieldMap$key.size() & i < builder.fieldMap$value.size(); i++) {
                fieldMap.put(builder.fieldMap$key.get(i), builder.fieldMap$value.get(i));
            }
        }

        qmsId = builder.qmsId;
        promotionName = builder.promotionName;
        promotionCategory = builder.promotionCategory == null ? PromotionCategory.NONE : builder.promotionCategory;

        title = DocumentTitleResolver.resolveTitle(builder.title, builder.reference);
    }

    public static class IdolSearchResultBuilder implements SearchResultBuilder {
        @Override
        public IdolSearchResult build() {
            return new IdolSearchResult(this);
        }
    }
}
