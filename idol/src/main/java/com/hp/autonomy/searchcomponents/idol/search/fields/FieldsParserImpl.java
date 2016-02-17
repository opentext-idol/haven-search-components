/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldAssociations;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FieldsParserImpl implements FieldsParser {
    private final ConfigService<? extends IdolSearchCapable> configService;

    @Autowired
    public FieldsParserImpl(final ConfigService<? extends IdolSearchCapable> configService) {
        this.configService = configService;
    }

    @Override
    public void parseDocumentFields(final Hit hit, final IdolSearchResult.Builder searchResultBuilder) {
        final FieldsInfo fieldsInfo = configService.getConfig().getFieldsInfo();
        final FieldAssociations fieldAssociations = fieldsInfo.getFieldAssociations();
        final Set<FieldInfo<?>> customFields = fieldsInfo.getCustomFields();

        final DocContent content = hit.getContent();
        if (content != null) {
            final Element docContent = (Element) content.getContent().get(0);
            if (docContent.hasChildNodes()) {
                final NodeList childNodes = docContent.getChildNodes();
                final Map<String, FieldInfo<?>> fieldMap = new HashMap<>(childNodes.getLength());

                for (final FieldInfo<?> customField : customFields) {
                    final List<?> values = parseFields(docContent, customField.getName(), customField.getType(), customField.getType().getType());
                    if (CollectionUtils.isNotEmpty(values)) {
                        addFieldToResultMap(fieldMap, customField, values);
                    }
                }

                searchResultBuilder
                        .setFieldMap(fieldMap)
                        .setContentType(readField(fieldAssociations.getMediaContentType(), fieldMap, String.class))
                        .setUrl(readField(fieldAssociations.getMediaUrl(), fieldMap, String.class))
                        .setOffset(readField(fieldAssociations.getMediaOffset(), fieldMap, String.class))
                        .setMmapUrl(readField(fieldAssociations.getMmapUrl(), fieldMap, String.class))
                        .setThumbnail(readField(fieldAssociations.getThumbnail(), fieldMap, String.class))
                        .setQmsId(parseField(docContent, IdolDocumentFieldsService.QMS_ID_FIELD_INFO, String.class))
                        .setPromotionCategory(determinePromotionCategory(docContent, hit.getPromotionname(), hit.getDatabase()));
            }
        }
    }

    private void addFieldToResultMap(final Map<String, FieldInfo<?>> fieldMap, final FieldInfo<?> fieldInfo, final List<?> values) {
        fieldMap.put(fieldInfo.getName(), new FieldInfo<>(fieldInfo.getName(), fieldInfo.getDisplayName(), fieldInfo.getType(), values));
    }

    private <T> T readField(final String name, final Map<String, FieldInfo<?>> fieldMap, final Class<T> type) {
        return fieldMap.containsKey(name) ? type.cast(fieldMap.get(name).getValues().iterator().next()) : null;
    }

    private PromotionCategory determinePromotionCategory(final Element docContent, final CharSequence promotionName, final CharSequence database) {
        final PromotionCategory promotionCategory;
        final Boolean injectedPromotion = parseField(docContent, IdolDocumentFieldsService.INJECTED_PROMOTION_FIELD_INFO, Boolean.class);
        if (injectedPromotion != null && injectedPromotion) {
            promotionCategory = PromotionCategory.CARDINAL_PLACEMENT;
        } else if (StringUtils.isNotEmpty(promotionName)) {
            // If the database isn't found, then assume it is a static content promotion
            promotionCategory = StringUtils.isNotEmpty(database) ? PromotionCategory.SPOTLIGHT : PromotionCategory.STATIC_CONTENT_PROMOTION;
        } else {
            promotionCategory = PromotionCategory.NONE;
        }

        return promotionCategory;
    }

    private <T> List<T> parseFields(final Element node, final String name, final FieldType fieldType, final Class<T> type) {
        final NodeList childNodes = node.getElementsByTagName(name.toUpperCase());
        final int length = childNodes.getLength();
        final List<T> values = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Node childNode = childNodes.item(i);
            values.add(fieldType.parseValue(type, childNode.getFirstChild().getNodeValue()));
        }

        return values;
    }

    private <T> T parseField(final Element node, final FieldInfo<T> fieldInfo, final Class<T> type) {
        final List<T> fields = parseFields(node, fieldInfo.getName(), fieldInfo.getType(), type);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }
}
