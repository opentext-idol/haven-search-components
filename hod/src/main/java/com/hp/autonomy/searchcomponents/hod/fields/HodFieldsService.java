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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class HodFieldsService implements FieldsService<HodFieldsRequest, HodErrorException> {
    private final RetrieveIndexFieldsService retrieveIndexFieldsService;

    @Autowired
    public HodFieldsService(final RetrieveIndexFieldsService retrieveIndexFieldsService) {
        this.retrieveIndexFieldsService = retrieveIndexFieldsService;
    }

    @Override
    @Cacheable(CacheNames.PARAMETRIC_FIELDS)
    public List<String> getParametricFields(final HodFieldsRequest request) throws HodErrorException {
        final Collection<FieldType> fieldTypes = Collections.singleton(FieldType.parametric);
        final RetrieveIndexFieldsResponse indexFields = retrieveIndexFields(request, fieldTypes);

        return indexFields.getParametricTypeFields();
    }

    private RetrieveIndexFieldsResponse retrieveIndexFields(final HodFieldsRequest request, final Collection<FieldType> fieldTypes) throws HodErrorException {
        final RetrieveIndexFieldsRequestBuilder fieldsParams = new RetrieveIndexFieldsRequestBuilder().setFieldTypes(fieldTypes).setMaxValues(request.getMaxValues());
        return retrieveIndexFieldsService.retrieveIndexFields(request.getDatabases(), fieldsParams);
    }

}
