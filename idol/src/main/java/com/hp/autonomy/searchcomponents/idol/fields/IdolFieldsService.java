/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.types.idol.GetTagNamesResponseData;
import com.hp.autonomy.types.requests.idol.actions.tags.TagActions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetTagNamesParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IdolFieldsService implements FieldsService<IdolFieldsRequest, AciErrorException> {
    private final AciService contentAciService;
    private final Processor<GetTagNamesResponseData> tagNamesResponseProcessor;

    @Autowired
    public IdolFieldsService(final AciService contentAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        this.contentAciService = contentAciService;
        tagNamesResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(GetTagNamesResponseData.class);
    }

    @Override
    @Cacheable(CacheNames.PARAMETRIC_FIELDS)
    public List<String> getParametricFields(final IdolFieldsRequest request) throws AciErrorException {
        return getTagNames(request, FieldTypeParam.Parametric);
    }

    private List<String> getTagNames(final FieldsRequest request, final FieldTypeParam fieldType) {
        final AciParameters aciParameters = new AciParameters(TagActions.GetTagNames.name());
        aciParameters.add(GetTagNamesParams.FieldType.name(), fieldType);
        aciParameters.add(GetTagNamesParams.MaxValues.name(), request.getMaxValues());

        final GetTagNamesResponseData responseData = contentAciService.executeAction(aciParameters, tagNamesResponseProcessor);

        final List<GetTagNamesResponseData.Name> names = responseData.getName();
        final List<String> tagNames = new ArrayList<>(names.size());
        for (final GetTagNamesResponseData.Name name : names) {
            final String value = name.getValue();
            tagNames.add(getFieldNameFromPath(value));
        }

        return tagNames;
    }

    private String getFieldNameFromPath(final String value) {
        return value.contains("/") ? value.substring(value.lastIndexOf('/') + 1) : value;
    }
}
