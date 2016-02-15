/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class FieldsParserImpl implements FieldsParser {
    @Override
    public void parseDocumentContent(final Hit hit, final IdolSearchResult.Builder searchResultBuilder) {
        final DocContent content = hit.getContent();
        if (content != null) {
            final Element docContent = (Element) content.getContent().get(0);
            searchResultBuilder
                    .setContentType(parseField(docContent, IdolSearchResult.CONTENT_TYPE_FIELD))
                    .setUrl(parseField(docContent, IdolSearchResult.URL_FIELD))
                    .setAuthors(parseFields(docContent, IdolSearchResult.AUTHOR_FIELD))
                    .setCategories(parseFields(docContent, IdolSearchResult.CATEGORY_FIELD))
                    .setDateCreated(parseDateField(docContent, Arrays.asList(IdolSearchResult.DATE_CREATED_FIELD, IdolSearchResult.CREATED_DATE_FIELD)))
                    .setDateModified(parseDateField(docContent, Arrays.asList(IdolSearchResult.MODIFIED_DATE_FIELD, IdolSearchResult.DATE_MODIFIED_FIELD)))
                    .setQmsId(parseField(docContent, IdolSearchResult.QMS_ID_FIELD))
                    .setThumbnail(parseField(docContent, IdolSearchResult.THUMBNAIL))
                    .setMmapUrl(parseField(docContent, IdolSearchResult.MMAP_URL))
                    .setPromotionCategory(determinePromotionCategory(docContent, hit.getPromotionname(), hit.getDatabase()));
        }
    }

    private PromotionCategory determinePromotionCategory(final Element docContent, final CharSequence promotionName, final CharSequence database) {
        final PromotionCategory promotionCategory;
        if (Boolean.parseBoolean(parseField(docContent, IdolSearchResult.INJECTED_PROMOTION_FIELD))) {
            promotionCategory = PromotionCategory.CARDINAL_PLACEMENT;
        } else if (StringUtils.isNotEmpty(promotionName)) {
            // If the database isn't found, then assume it is a static content promotion
            promotionCategory = StringUtils.isNotEmpty(database) ? PromotionCategory.SPOTLIGHT : PromotionCategory.STATIC_CONTENT_PROMOTION;
        } else {
            promotionCategory = PromotionCategory.NONE;
        }

        return promotionCategory;
    }

    private List<String> parseFields(final Element node, final Iterable<String> fieldNames) {
        final List<String> values = new ArrayList<>();

        final Iterator<String> iterator = fieldNames.iterator();
        while (iterator.hasNext() && values.isEmpty()) {
            values.addAll(parseFields(node, iterator.next()));
        }

        return values;
    }

    private List<String> parseFields(final Element node, final String fieldName) {
        final NodeList childNodes = node.getElementsByTagName(fieldName.toUpperCase());
        final int length = childNodes.getLength();
        final List<String> values = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Node childNode = childNodes.item(i);
            values.add(childNode.getFirstChild().getNodeValue());
        }

        return values;
    }

    private String parseField(final Element node, final Iterable<String> fieldNames) {
        final List<String> fields = parseFields(node, fieldNames);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }

    private String parseField(final Element node, final String fieldName) {
        final List<String> fields = parseFields(node, fieldName);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }

    private DateTime parseDateField(final Element node, final Iterable<String> fieldNames) {
        final String stringField = parseField(node, fieldNames);
        return stringField != null ? DateTime.parse(stringField) : null;
    }
}
