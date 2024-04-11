/*
 * Copyright 2015 Open Text.
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

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.types.requests.idol.actions.tags.TagActions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetTagNamesParams;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.GetTagNamesResponseData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.fields.FieldsService.FIELD_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link FieldsService}: sends a GetTagNames action for each desired fieldType and parses the responses into a map
 */
@SuppressWarnings("WeakerAccess")
@Service(FIELD_SERVICE_BEAN_NAME)
@IdolService
class IdolFieldsServiceImpl implements IdolFieldsService {
    private final AciService contentAciService;
    private final TagNameFactory tagNameFactory;
    private final Processor<GetTagNamesResponseData> tagNamesResponseProcessor;

    @Autowired
    IdolFieldsServiceImpl(final AciService contentAciService,
                          final TagNameFactory tagNameFactory,
                          final ProcessorFactory aciResponseProcessorFactory) {
        this.contentAciService = contentAciService;
        this.tagNameFactory = tagNameFactory;
        tagNamesResponseProcessor = aciResponseProcessorFactory.getResponseDataProcessor(GetTagNamesResponseData.class);
    }

    @Override
    @Cacheable(CacheNames.FIELDS)
    public Map<FieldTypeParam, Set<TagName>> getFields(final IdolFieldsRequest request) throws AciErrorException {
        final ActionParameters aciParameters = new ActionParameters(TagActions.GetTagNames.name());
        Optional.ofNullable(request.getFieldTypes()).ifPresent(fieldTypes ->
                aciParameters.add(GetTagNamesParams.FieldType.name(), String.join(",", fieldTypes.stream().map(FieldTypeParam::name).collect(Collectors.toList()))));
        aciParameters.add(GetTagNamesParams.MaxValues.name(), request.getMaxValues());
        aciParameters.add(GetTagNamesParams.TypeDetails.name(), true);

        final GetTagNamesResponseData responseData = contentAciService.executeAction(aciParameters, tagNamesResponseProcessor);

        return responseData.getName().stream()
                .filter(name -> name.getTypes() != null)
                .flatMap(name -> Arrays.stream(name.getTypes().split(",")).map(type -> new ImmutablePair<>(type, name.getValue())))
                .filter(entry -> recognisableType(entry.getLeft()))
                .collect(Collectors.groupingBy(entry -> FieldTypeParam.fromValue(entry.getKey()), Collectors.mapping(entry -> tagNameFactory.buildTagName(entry.getValue()), Collectors.toSet())));
    }

    private boolean recognisableType(final String type) {
        try {
            FieldTypeParam.fromValue(type);
            return true;
        } catch (final IllegalArgumentException ignored) {
            return false;
        }
    }
}
