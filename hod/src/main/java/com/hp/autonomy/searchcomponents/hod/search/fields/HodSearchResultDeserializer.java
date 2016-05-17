/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search.fields;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.textindex.query.search.PromotionType;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HodSearchResultDeserializer extends JsonDeserializer<HodSearchResult> {
    private final ConfigService<? extends HodSearchCapable> configService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HodSearchResultDeserializer(final ConfigService<? extends HodSearchCapable> configService) {
        this.configService = configService;
    }

    @Override
    public HodSearchResult deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final FieldsInfo fieldsInfo = configService.getConfig().getFieldsInfo();
        final Map<String, FieldInfo<?>> fieldConfig = fieldsInfo.getFieldConfig();

        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        final Map<String, FieldInfo<?>> fieldMap = new HashMap<>(fieldConfig.size());
        for (final FieldInfo<?> fieldInfo : fieldConfig.values()) {
            for (final String name : fieldInfo.getNames()) {
                final String[] stringValues = parseAsStringArray(node, name);

                if (ArrayUtils.isNotEmpty(stringValues)) {
                    final List<Object> values = new ArrayList<>(stringValues.length);
                    for (final String stringValue : stringValues) {
                        final Object value = fieldInfo.getType().parseValue(fieldInfo.getType().getType(), stringValue);
                        values.add(value);
                    }

                    if (!fieldMap.containsKey(fieldInfo.getId())){
                        fieldMap.put(fieldInfo.getId(), new FieldInfo<>(fieldInfo.getId(), Collections.singleton(name), fieldInfo.getType(), values));
                    }
                    else {
                        final FieldInfo<?> existingFieldInfo = fieldMap.get(fieldInfo.getId());
                        //noinspection unchecked
                        ((List<Object>) existingFieldInfo.getValues()).addAll(values);
                        existingFieldInfo.getNames().add(name);
                    }
                }
            }
        }

        return new HodSearchResult.Builder()
                .setReference(parseAsString(node, "reference"))
                .setIndex(parseAsString(node, "index"))
                .setTitle(parseAsString(node, "title"))
                .setSummary(parseAsString(node, "summary"))
                .setWeight(parseAsDouble(node, "weight"))
                .setFieldMap(fieldMap)
                .setDate(parseAsDateFromArray(node, "date"))
                .setPromotionCategory(parsePromotionCategory(node, "promotion"))
                .build();
    }

    private String parseAsString(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final JsonNode jsonNode = node.get(fieldName);
        return jsonNode != null ? objectMapper.treeToValue(jsonNode, String.class) : null;
    }

    private String[] parseAsStringArray(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final JsonNode jsonNode = node.get(fieldName);
        return jsonNode != null ? objectMapper.treeToValue(jsonNode, String[].class) : null;
    }

    private String parseAsStringFromArray(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final String[] values = parseAsStringArray(node, fieldName);
        return ArrayUtils.isNotEmpty(values) ? values[0] : null;
    }

    private Double parseAsDouble(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final String value = parseAsString(node, fieldName);
        return value != null ? Double.parseDouble(value) : null;
    }

    private DateTime parseAsDateFromArray(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final String value = parseAsStringFromArray(node, fieldName);
        return value != null ? FieldType.DATE.parseValue(DateTime.class, value) : null;
    }

    private PromotionCategory parsePromotionCategory(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final String value = parseAsString(node, fieldName);

        PromotionCategory promotionCategory = null;
        if (value != null) {
            final PromotionType promotionType = PromotionType.valueOf(value);
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
        }

        return promotionCategory;
    }
}
