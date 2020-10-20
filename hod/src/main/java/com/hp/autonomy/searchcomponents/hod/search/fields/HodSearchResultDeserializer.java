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

package com.hp.autonomy.searchcomponents.hod.search.fields;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.textindex.query.search.PromotionType;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@JsonComponent
public class HodSearchResultDeserializer extends JsonDeserializer<HodSearchResult> {
    /**
     * Properties on JSON documents returned from HOD which should not be added to the HodSearchResult fields map. Fields
     * that are not JSON arrays do not need to be listed here.
     */
    private static final Set<String> IGNORED_PROPERTIES = ImmutableSet.<String>builder()
        .add("links")
        .build();

    private final ConfigService<? extends HodSearchCapable> configService;
    private final FieldDisplayNameGenerator fieldDisplayNameGenerator;
    private final FieldPathNormaliser fieldPathNormaliser;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HodSearchResultDeserializer(
        final ConfigService<? extends HodSearchCapable> configService,
        final FieldDisplayNameGenerator fieldDisplayNameGenerator,
        final FieldPathNormaliser fieldPathNormaliser
    ) {
        this.configService = configService;
        this.fieldDisplayNameGenerator = fieldDisplayNameGenerator;
        this.fieldPathNormaliser = fieldPathNormaliser;
    }

    @Override
    public HodSearchResult deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final Map<FieldPath, FieldInfo<?>> fieldConfigByName = configService.getConfig().getFieldsInfo().getFieldConfigByName();

        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        final Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
        final Iterable<Map.Entry<String, JsonNode>> entryIterable = () -> fieldsIterator;
        final Stream<Map.Entry<String, JsonNode>> entryStream = StreamSupport.stream(entryIterable.spliterator(), false);

        final Map<String, FieldInfo<?>> fieldMap = entryStream
            .filter(entry -> entry.getValue().getNodeType() == JsonNodeType.ARRAY && !IGNORED_PROPERTIES.contains(entry.getKey()))
            .reduce(
                ImmutableMap.of(),
                (map, entry) -> {
                    final FieldPath fieldPath = fieldPathNormaliser.normaliseFieldPath(entry.getKey());
                    final List<String> stringValues = parseNodeAsStringList(entry.getValue());

                    // Config field info may or may not have a display name and it may contain friendly value names
                    final Optional<FieldInfo<?>> maybeConfigFieldInfo = Optional.ofNullable(fieldConfigByName.get(fieldPath));

                    // If there is a config entry for this field, we may have seen one with the same ID already
                    final Optional<FieldInfo<?>> maybeExistingFieldInfo = maybeConfigFieldInfo.flatMap(configFieldInfo -> Optional.ofNullable(map.get(configFieldInfo.getId())));

                    final FieldInfo<?> newFieldInfo = reduceFieldInfo(fieldPath, stringValues, maybeConfigFieldInfo, maybeExistingFieldInfo);
                    final String id = newFieldInfo.getId();

                    final ImmutableMap.Builder<String, FieldInfo<?>> builder = ImmutableMap.builder();
                    builder.put(id, newFieldInfo);

                    map.entrySet().stream()
                        .filter(existingEntry -> !existingEntry.getKey().equals(id))
                        .forEach(builder::put);

                    return builder.build();
                },
                this::mergeMaps
            );

        return HodSearchResult.builder()
            .reference(parseAsString(node, "reference"))
            .index(parseAsString(node, "index"))
            .title(parseAsString(node, "title"))
            .summary(parseAsString(node, "summary"))
            .weight(parseAsDouble(node, "weight"))
            .fieldMap(fieldMap)
            .date(parseAsDateFromArray(node, "date"))
            .promotionCategory(parsePromotionCategory(node, "promotion"))
            .build();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private FieldInfo<?> reduceFieldInfo(
        final FieldPath fieldPath,
        final Collection<String> stringValues,
        final Optional<FieldInfo<?>> maybeConfigFieldInfo,
        final Optional<FieldInfo<?>> maybeExistingFieldInfo
    ) {
        if(maybeExistingFieldInfo.isPresent()) {
            final FieldInfo<?> existingFieldInfo = maybeExistingFieldInfo.get();
            final String id = existingFieldInfo.getId();
            final FieldType fieldType = existingFieldInfo.getType();

            return existingFieldInfo.toBuilder()
                .name(fieldPath)
                .values(parseValues(fieldType, id, stringValues))
                .build();
        } else {
            final String id;
            final FieldType fieldType;
            final boolean advanced;

            if(maybeConfigFieldInfo.isPresent()) {
                final FieldInfo<?> configFieldInfo = maybeConfigFieldInfo.get();
                id = configFieldInfo.getId();
                advanced = configFieldInfo.isAdvanced();
                fieldType = configFieldInfo.getType();
            } else {
                id = fieldPath.getNormalisedPath();
                advanced = true;
                fieldType = FieldType.STRING;
            }

            return FieldInfo.builder()
                .id(id)
                .name(fieldPath)
                .displayName(fieldDisplayNameGenerator.generateDisplayNameFromId(id))
                .type(fieldType)
                .values(parseValues(fieldType, id, stringValues))
                .advanced(advanced)
                .build();
        }
    }

    private <K, V> ImmutableMap<K, V> mergeMaps(final Map<K, V> map1, final Map<K, V> map2) {
        return ImmutableMap.<K, V>builder()
            .putAll(map1)
            .putAll(map2)
            .build();
    }

    private <T extends Serializable> Collection<FieldValue<T>> parseValues(final FieldType fieldType, final String fieldId, final Collection<String> stringValues) {
        return stringValues.stream()
            .map(stringValue -> {
                @SuppressWarnings("unchecked") final T value = (T)fieldType.parseValue(fieldType.getType(), stringValue);
                final String displayValue = fieldDisplayNameGenerator.generateDisplayValueFromId(fieldId, value, fieldType);
                return new FieldValue<>(value, displayValue);
            })
            .collect(Collectors.toList());
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

    @SuppressWarnings("SameParameterValue")
    private Double parseAsDouble(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final String value = parseAsString(node, fieldName);
        return value != null ? Double.parseDouble(value) : null;
    }

    @SuppressWarnings("SameParameterValue")
    private ZonedDateTime parseAsDateFromArray(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final String value = parseAsStringFromArray(node, fieldName);
        return value != null ? FieldType.DATE.parseValue(ZonedDateTime.class, value) : null;
    }

    private List<String> parseNodeAsStringList(final TreeNode node) {
        try {
            return Arrays.asList(objectMapper.treeToValue(node, String[].class));
        } catch(final JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse JSON array", e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private PromotionCategory parsePromotionCategory(@SuppressWarnings("TypeMayBeWeakened") final JsonNode node, final String fieldName) throws JsonProcessingException {
        final String value = parseAsString(node, fieldName);

        PromotionCategory promotionCategory = null;
        if(value != null) {
            final PromotionType promotionType = PromotionType.valueOf(value);
            switch(promotionType) {
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
