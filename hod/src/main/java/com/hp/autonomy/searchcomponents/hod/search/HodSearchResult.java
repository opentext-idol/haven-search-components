/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.search.DocumentTitleResolver;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Data
@Builder(toBuilder = true)
public class HodSearchResult implements SearchResult {
    private static final long serialVersionUID = -7386227266595690038L;

    private static final Pattern PATH_SEPARATOR_REGEX = Pattern.compile("/|\\\\");
    private static final String PROMOTION_PROPERTY = "promotion";

    private final String reference;
    private final String index;

    private final String title;
    private final String summary;
    private final Double weight;

    @Singular("fieldEntry")
    private final Map<String, FieldInfo<?>> fieldMap;

    private final ZonedDateTime date;

    private final PromotionCategory promotionCategory;

    private final String domain;

    private HodSearchResult(final HodSearchResultBuilder builder) {
        reference = builder.reference;
        index = builder.index;

        summary = builder.summary;
        weight = builder.weight;
        fieldMap = new HashMap<>();

        if(builder.fieldMap$key != null && builder.fieldMap$value != null) {
            for(int i = 0; i < builder.fieldMap$key.size() & i < builder.fieldMap$value.size(); i++) {
                fieldMap.put(builder.fieldMap$key.get(i), builder.fieldMap$value.get(i));
            }
        }

        date = builder.date;
        promotionCategory = builder.promotionCategory == null ? PromotionCategory.NONE : builder.promotionCategory;
        domain = builder.domain;

        title = DocumentTitleResolver.resolveTitle(builder.title, builder.reference);
    }

    @SuppressWarnings("WeakerAccess")
    public static class HodSearchResultBuilder implements SearchResult.SearchResultBuilder {
        @Override
        public HodSearchResult build() {
            return new HodSearchResult(this);
        }
    }
}
