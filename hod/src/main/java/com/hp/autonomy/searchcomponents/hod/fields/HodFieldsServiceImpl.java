/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
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

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.api.textindex.query.fields.FieldType;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsResponse;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.fields.FieldsService.FIELD_SERVICE_BEAN_NAME;

/**
 * Default HoD implementation of {@link FieldsService}: retrieves lists of field names for the supplied types
 */
@Service(FIELD_SERVICE_BEAN_NAME)
class HodFieldsServiceImpl implements HodFieldsService {
    private final RetrieveIndexFieldsService retrieveIndexFieldsService;
    private final TagNameFactory tagNameFactory;

    @Autowired
    HodFieldsServiceImpl(final RetrieveIndexFieldsService retrieveIndexFieldsService, final TagNameFactory tagNameFactory) {
        this.retrieveIndexFieldsService = retrieveIndexFieldsService;
        this.tagNameFactory = tagNameFactory;
    }

    @Override
    @Cacheable(CacheNames.FIELDS)
    public Map<FieldTypeParam, Set<TagName>> getFields(final HodFieldsRequest request) throws HodErrorException {
        final Collection<FieldType> fieldTypeList = request.getFieldTypes().stream()
                .map(FieldType::fromParam)
                .collect(Collectors.toList());

        final Map<FieldTypeParam, List<String>> fields = retrieveIndexFields(request, fieldTypeList).getFields();

        return fields.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(tagNameFactory::buildTagName).collect(Collectors.toSet())));
    }

    private RetrieveIndexFieldsResponse retrieveIndexFields(final HodFieldsRequest request, final Collection<FieldType> fieldTypes) throws HodErrorException {
        final RetrieveIndexFieldsRequestBuilder fieldsParams = new RetrieveIndexFieldsRequestBuilder().setFieldTypes(fieldTypes).setMaxValues(request.getMaxValues());
        return retrieveIndexFieldsService.retrieveIndexFields(request.getDatabases(), fieldsParams);
    }
}
