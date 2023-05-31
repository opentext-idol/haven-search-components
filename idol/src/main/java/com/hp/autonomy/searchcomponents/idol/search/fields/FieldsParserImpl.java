/*
 * Copyright 2015-2018 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.*;

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
        final CaseInsensitiveMap<String, FieldInfo<?>> fieldMap = new CaseInsensitiveMap<>();
        String qmsId = null;
        PromotionCategory promotionCategory = PromotionCategory.NONE;
        if (content != null) {
            final Element docContent = (Element) content.getContent().get(0);
            if (docContent.hasChildNodes()) {
                parseAllFields(fieldConfig, docContent, fieldMap, docContent.getNodeName());
                qmsId = parseField(docContent, documentFieldsService.getQmsIdFieldInfo(), String.class);
                promotionCategory = determinePromotionCategory(docContent, hit.getPromotionname(), hit.getDatabase());
            }
        }

        searchResultBuilder
                .fieldMap(fieldMap)
                .qmsId(qmsId)
                .promotionCategory(promotionCategory);
    }

    /**
     * Add a parsed field to the result.
     *
     * @param fieldMap Result fields
     * @param fieldInfo Field definition, configured or computed
     * @param fieldPath Full path to the field from the document root
     * @param value Parsed field value
     * @param <T> Parsed field value type
     */
    private <T extends Serializable> void addToFieldMap(final Map<String, FieldInfo<?>> fieldMap, final FieldInfo<T> fieldInfo, final FieldPath fieldPath, final FieldValue<T> value) {
        final String id = fieldInfo.getId();
        final String displayName = fieldDisplayNameGenerator.generateDisplayNameFromId(id);
        if (fieldMap.containsKey(id)) {
            @SuppressWarnings({"unchecked", "CastToConcreteClass"})
            final FieldInfo<T> updatedFieldInfo = ((FieldInfo<T>) fieldMap.get(id)).toBuilder()
                .name(fieldPath)
                .value(value)
                .build();
            fieldMap.put(id, updatedFieldInfo);
        } else {
            final Collection<FieldPath> names = new ArrayList<>(fieldInfo.getNames().size());
            names.add(fieldPath);
            final FieldInfo<T> fieldInfoWithValue = FieldInfo.<T>builder()
                .id(id)
                .names(names)
                .displayName(displayName)
                .type(fieldInfo.getType())
                .advanced(fieldInfo.isAdvanced())
                .value(value)
                .build();
            fieldMap.put(id, fieldInfoWithValue);
        }
    }

    private void parseAllFields(final Map<FieldPath, FieldInfo<?>> fieldConfig, final Node node, final Map<String, FieldInfo<?>> fieldMap, final String name) {
        final FieldPath fieldPath = fieldPathNormaliser.normaliseFieldPath(name);
        final FieldInfo<Serializable> fieldInfo = getFieldInfo(fieldConfig, fieldPath);

        // fields configured as records have separate handling for nested fields, so don't fall
        // through to the recursive call below
        if (fieldInfo.getType().equals(FieldType.RECORD)) {
            final Serializable value = RecordType.parseValue(node);
            addToFieldMap(fieldMap, fieldInfo, fieldPath, new FieldValue<>(value, null));
            return;
        }

        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node childNode = childNodes.item(i);
            if (childNode instanceof Text) {
                final String stringValue = childNode.getNodeValue();
                if (StringUtils.isNotBlank(stringValue)) {
                    final String id = fieldInfo.getId();
                    final FieldType fieldType = fieldInfo.getType();
                    final Serializable value = (Serializable) fieldType.parseValue(fieldType.getType(), stringValue);
                    final String displayValue = fieldDisplayNameGenerator.generateDisplayValueFromId(id, value, fieldType);
                    addToFieldMap(fieldMap, fieldInfo, fieldPath, new FieldValue<>(value, displayValue));
                }

            } else {
                final FieldPath childPath = fieldPathNormaliser.normaliseFieldPath(name + '/' + childNode.getNodeName());
                final FieldInfo<Serializable> childFieldInfo = getFieldInfo(fieldConfig, childPath);

                if (fieldConfig.containsKey(childPath) && fieldConfig.get(childPath).getChildMapping() != null) {
                    final String id = childFieldInfo.getId();
                    final FieldType fieldType = childFieldInfo.getType();
                    final LinkedHashMap<String, Serializable> value = childFieldInfo.getChildMapping().parseMapType(fieldType, childNode);
                    String displayValue = null;
                    if(!value.isEmpty()) {
                        displayValue = fieldDisplayNameGenerator.generateDisplayValueFromId(id, value.values().iterator().next(), fieldType);
                    }
                    addToFieldMap(fieldMap, childFieldInfo, childPath, new FieldValue<Serializable>(value, displayValue));
                }

                // We still want to process the children, e.g. LAT is used for both Places and Location
                parseAllFields(fieldConfig, childNode, fieldMap, name + '/' + childNode.getNodeName());
            }
        }
    }

    private <T extends Serializable> FieldInfo<T> getFieldInfo(final Map<FieldPath, FieldInfo<?>> fieldConfig, final FieldPath fieldPath) {
        return fieldConfig.containsKey(fieldPath) ? (FieldInfo<T>) fieldConfig.get(fieldPath) : FieldInfo.<T>builder()
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
