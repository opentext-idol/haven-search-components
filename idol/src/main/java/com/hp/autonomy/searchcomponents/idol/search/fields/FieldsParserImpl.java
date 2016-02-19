/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final Map<String, FieldInfo<?>> fieldConfig = fieldsInfo.getFieldConfigByName();

        final DocContent content = hit.getContent();
        Map<String, FieldInfo<?>> fieldMap = Collections.emptyMap();
        String qmsId = null;
        PromotionCategory promotionCategory = PromotionCategory.NONE;
        if (content != null) {
            final Element docContent = (Element) content.getContent().get(0);
            if (docContent.hasChildNodes()) {
                final NodeList childNodes = docContent.getChildNodes();
                fieldMap = new HashMap<>(childNodes.getLength());

                parseAllFields(fieldConfig, childNodes, fieldMap, docContent.getNodeName());

                qmsId = parseField(docContent, IdolDocumentFieldsService.QMS_ID_FIELD_INFO, String.class);
                promotionCategory = determinePromotionCategory(docContent, hit.getPromotionname(), hit.getDatabase());
            }
        }

        searchResultBuilder
                .setFieldMap(fieldMap)
                .setQmsId(qmsId)
                .setPromotionCategory(promotionCategory);
    }

    private void parseAllFields(final Map<String, FieldInfo<?>> fieldConfig, final NodeList childNodes, final Map<String, FieldInfo<?>> fieldMap, final String name) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node node = childNodes.item(i);
            if (node instanceof Text) {
                final String stringValue = node.getNodeValue();
                if (StringUtils.isNotBlank(stringValue)) {
                    final FieldInfo<?> fieldInfo = getFieldInfo(fieldConfig, name);
                    final String id = fieldInfo.getId();
                    final FieldType fieldType = fieldInfo.getType();
                    final Object value = fieldType.parseValue(fieldType.getType(), stringValue);
                    if (fieldMap.containsKey(id)) {
                        //noinspection unchecked,CastToConcreteClass
                        ((FieldInfo<Object>) fieldMap.get(id)).getValues().add(value);
                    } else {
                        fieldMap.put(id, new FieldInfo<>(id, Collections.singletonList(name), fieldInfo.getType(), value));
                    }
                }
            } else if (node.getChildNodes().getLength() > 0) {
                parseAllFields(fieldConfig, node.getChildNodes(), fieldMap, node.getNodeName());
            }
        }
    }

    private FieldInfo<?> getFieldInfo(final Map<String, FieldInfo<?>> fieldConfig, final String name) {
        return fieldConfig.containsKey(name) ? fieldConfig.get(name) : new FieldInfo<>(name, Collections.singletonList(name), FieldType.STRING);
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
        final List<T> fields = parseFields(node, fieldInfo.getNames().get(0), fieldInfo.getType(), type);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }
}
