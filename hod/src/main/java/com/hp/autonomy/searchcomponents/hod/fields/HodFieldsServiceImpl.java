/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.api.textindex.query.fields.FieldType;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsResponse;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.hp.autonomy.searchcomponents.core.fields.FieldsService.FIELD_SERVICE_BEAN_NAME;

/**
 * Default HoD implementation of {@link FieldsService}: retrieves lists of field names for the supplied types
 */
@SuppressWarnings("WeakerAccess")
@Service(FIELD_SERVICE_BEAN_NAME)
class HodFieldsServiceImpl implements HodFieldsService {
    private final RetrieveIndexFieldsService retrieveIndexFieldsService;

    @Autowired
    HodFieldsServiceImpl(final RetrieveIndexFieldsService retrieveIndexFieldsService) {
        this.retrieveIndexFieldsService = retrieveIndexFieldsService;
    }

    @Override
    @Cacheable(CacheNames.FIELDS)
    public Map<FieldTypeParam, List<TagName>> getFields(final HodFieldsRequest request, final FieldTypeParam... fieldTypes) throws HodErrorException {
        final Collection<FieldType> fieldTypeList = new ArrayList<>(fieldTypes.length);
        for (final FieldTypeParam fieldType : fieldTypes) {
            fieldTypeList.add(FieldType.fromParam(fieldType));
        }
        return retrieveIndexFields(request, fieldTypeList).getFields();
    }

    private RetrieveIndexFieldsResponse retrieveIndexFields(final HodFieldsRequest request, final Collection<FieldType> fieldTypes) throws HodErrorException {
        final RetrieveIndexFieldsRequestBuilder fieldsParams = new RetrieveIndexFieldsRequestBuilder().setFieldTypes(fieldTypes).setMaxValues(request.getMaxValues());
        return retrieveIndexFieldsService.retrieveIndexFields(request.getDatabases(), fieldsParams);
    }

}
