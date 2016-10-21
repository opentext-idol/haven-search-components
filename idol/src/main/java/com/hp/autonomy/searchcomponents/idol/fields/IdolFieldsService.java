/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.GetTagNamesResponseData;
import com.hp.autonomy.types.requests.idol.actions.tags.TagActions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetTagNamesParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
@Service
public class IdolFieldsService implements FieldsService<IdolFieldsRequest, AciErrorException> {
    private final AciService contentAciService;
    private final Processor<GetTagNamesResponseData> tagNamesResponseProcessor;

    @Autowired
    public IdolFieldsService(final AciService contentAciService, final ProcessorFactory aciResponseProcessorFactory) {
        this.contentAciService = contentAciService;
        tagNamesResponseProcessor = aciResponseProcessorFactory.getResponseDataProcessor(GetTagNamesResponseData.class);
    }

    @Override
    @Cacheable(CacheNames.FIELDS)
    public Map<FieldTypeParam, List<TagName>> getFields(final IdolFieldsRequest request, final FieldTypeParam... fieldTypes) throws AciErrorException {
        final Map<FieldTypeParam, List<TagName>> results = new EnumMap<>(FieldTypeParam.class);
        for (final FieldTypeParam fieldType : fieldTypes) {
            results.put(fieldType, getTagNames(request, fieldType));
        }

        return results;
    }

    private List<TagName> getTagNames(final FieldsRequest request, final FieldTypeParam fieldType) {
        final AciParameters aciParameters = new AciParameters(TagActions.GetTagNames.name());
        aciParameters.add(GetTagNamesParams.FieldType.name(), fieldType);
        aciParameters.add(GetTagNamesParams.MaxValues.name(), request.getMaxValues());

        final GetTagNamesResponseData responseData = contentAciService.executeAction(aciParameters, tagNamesResponseProcessor);

        final List<GetTagNamesResponseData.Name> names = responseData.getName();
        final List<TagName> tagNames = new ArrayList<>(names.size());
        for (final GetTagNamesResponseData.Name name : names) {
            final String value = name.getValue();
            tagNames.add(new TagName(value));
        }

        return tagNames;
    }
}
