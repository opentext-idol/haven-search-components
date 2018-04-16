/*
 * Copyright 2015-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.idol.responses.DocContent;
import com.hp.autonomy.types.idol.responses.Hit;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import java.util.LinkedHashMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hp.autonomy.searchcomponents.idol.search.fields.FieldsParser.FIELDS_PARSER_BEAN_NAME;

/**
 * Default implementation of {@link FieldsParser}
 */
@Component(FIELDS_PARSER_BEAN_NAME)
class FieldsParserImpl implements FieldsParser {
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final FieldPathNormaliser fieldPathNormaliser;
    private final FieldDisplayNameGenerator fieldDisplayNameGenerator;
    private final IdolDocumentFieldsService documentFieldsService;

    @Autowired
    FieldsParserImpl(final ConfigService<? extends IdolSearchCapable> configService,
                     final FieldPathNormaliser fieldPathNormaliser,
                     final FieldDisplayNameGenerator fieldDisplayNameGenerator,
                     final IdolDocumentFieldsService documentFieldsService) {
        this.configService = configService;
        this.fieldPathNormaliser = fieldPathNormaliser;
        this.fieldDisplayNameGenerator = fieldDisplayNameGenerator;
        this.documentFieldsService = documentFieldsService;
    }

    @Override
    public void parseDocumentFields(final Hit hit, final IdolSearchResult.IdolSearchResultBuilder searchResultBuilder) {
        final FieldsInfo fieldsInfo = configService.getConfig().getFieldsInfo();
        final Map<FieldPath, FieldInfo<?>> fieldConfig = fieldsInfo.getFieldConfigByName();

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
                qmsId = parseField(docContent, documentFieldsService.getQmsIdFieldInfo(), String.class);
                promotionCategory = determinePromotionCategory(docContent, hit.getPromotionname(), hit.getDatabase());
            }
        }

        searchResultBuilder
                .fieldMap(fieldMap)
                .qmsId(qmsId)
                .promotionCategory(promotionCategory);
    }

    private void parseAllFields(final Map<FieldPath, FieldInfo<?>> fieldConfig, final NodeList childNodes, final Map<String, FieldInfo<?>> fieldMap, final String name) {
        final FieldPath fieldPath = fieldPathNormaliser.normaliseFieldPath(name);
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node node = childNodes.item(i);
            if (node instanceof Text) {
                final String stringValue = node.getNodeValue();
                if (StringUtils.isNotBlank(stringValue)) {
                    final FieldInfo<?> fieldInfo = getFieldInfo(fieldConfig, fieldPath);
                    final String id = fieldInfo.getId();
                    final String displayName = fieldDisplayNameGenerator.generateDisplayNameFromId(id);
                    final FieldType fieldType = fieldInfo.getType();
                    final Serializable value = (Serializable) fieldType.parseValue(fieldType.getType(), stringValue);
                    final String displayValue = fieldDisplayNameGenerator.generateDisplayValueFromId(id, value, fieldType);
                    if (fieldMap.containsKey(id)) {
                        @SuppressWarnings({"unchecked", "CastToConcreteClass"})
                        final FieldInfo<?> updatedFieldInfo = ((FieldInfo<Serializable>) fieldMap.get(id)).toBuilder()
                                .name(fieldPath)
                                .value(new FieldValue<>(value, displayValue))
                                .build();
                        fieldMap.put(id, updatedFieldInfo);
                    } else {
                        final Collection<FieldPath> names = new ArrayList<>(fieldInfo.getNames().size());
                        names.add(fieldPath);
                        final FieldInfo<Serializable> fieldInfoWithValue = FieldInfo.builder()
                                .id(id)
                                .names(names)
                                .displayName(displayName)
                                .type(fieldInfo.getType())
                                .advanced(fieldInfo.isAdvanced())
                                .value(new FieldValue<>(value, displayValue))
                                .build();
                        fieldMap.put(id, fieldInfoWithValue);
                    }
                }
            } else {
                final FieldPath childPath = fieldPathNormaliser.normaliseFieldPath(name + '/' + node.getNodeName());
                if (fieldConfig.containsKey(childPath) && fieldConfig.get(childPath).getChildMapping() != null) {
                    final FieldInfo<?> fieldInfo = fieldConfig.get(childPath);
                    final String id = fieldInfo.getId();
                    final String displayName = fieldDisplayNameGenerator.generateDisplayNameFromId(id);
                    final FieldType fieldType = fieldInfo.getType();

                    final LinkedHashMap<String, Serializable> value = fieldInfo.getChildMapping().parseMapType(fieldType, node);

                    String displayValue = null;
                    if(!value.isEmpty()) {
                        displayValue = fieldDisplayNameGenerator.generateDisplayValueFromId(id, value.values().iterator().next(), fieldType);
                    }

                    if(fieldMap.containsKey(id)) {
                        @SuppressWarnings({"unchecked", "CastToConcreteClass"}) final FieldInfo<?> updatedFieldInfo = ((FieldInfo<LinkedHashMap<String, Serializable>>) fieldMap.get(id)).toBuilder()
                                .name(childPath)
                                .value(new FieldValue<>(value, displayValue))
                                .build();
                        fieldMap.put(id, updatedFieldInfo);
                    }
                    else {
                        final Collection<FieldPath> names = new ArrayList<>(fieldInfo.getNames().size());
                        names.add(childPath);
                        final FieldInfo<LinkedHashMap<String, Serializable>> fieldInfoWithValue = FieldInfo.<LinkedHashMap<String, Serializable>>builder()
                                .id(id)
                                .names(names)
                                .displayName(displayName)
                                .type(fieldInfo.getType())
                                .advanced(fieldInfo.isAdvanced())
                                .value(new FieldValue<>(value, displayValue))
                                .childMapping(fieldInfo.getChildMapping())
                                .build();
                        fieldMap.put(id, fieldInfoWithValue);
                    }
                }
                else if (node.getChildNodes().getLength() > 0) {
                    parseAllFields(fieldConfig, node.getChildNodes(), fieldMap, name + '/' + node.getNodeName());
                }
            }
        }
    }

    private FieldInfo<?> getFieldInfo(final Map<FieldPath, FieldInfo<?>> fieldConfig, final FieldPath fieldPath) {
        return fieldConfig.containsKey(fieldPath) ? fieldConfig.get(fieldPath) : FieldInfo.builder()
                .id(fieldPath.getNormalisedPath())
                .name(fieldPath)
                .advanced(true)
                .build();
    }

    private PromotionCategory determinePromotionCategory(final Element docContent, final CharSequence promotionName, final CharSequence database) {
        final PromotionCategory promotionCategory;
        final Boolean injectedPromotion = parseField(docContent, documentFieldsService.getInjectedPromotionFieldInfo(), Boolean.class);
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

    private <T> List<T> parseFields(final Element node, final FieldPath fieldPath, final FieldType fieldType, final Class<T> type) {
        final NodeList childNodes = node.getElementsByTagName(fieldPath.getFieldName());
        final int length = childNodes.getLength();
        final List<T> values = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Node childNode = childNodes.item(i);
            values.add(fieldType.parseValue(type, childNode.getFirstChild().getNodeValue()));
        }

        return values;
    }

    private <T extends Serializable> T parseField(final Element node, final FieldInfo<T> fieldInfo, final Class<T> type) {
        final List<T> fields = parseFields(node, fieldInfo.getNames().iterator().next(), fieldInfo.getType(), type);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }
}
