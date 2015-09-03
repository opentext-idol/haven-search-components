/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.fields;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.fields.FieldType;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsResponse;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.error.HodErrorException;

import java.util.HashSet;
import java.util.Set;

public class IndexFieldsServiceImpl implements IndexFieldsService {

    private final RetrieveIndexFieldsService retrieveIndexFieldsService;

    public IndexFieldsServiceImpl(final RetrieveIndexFieldsService retrieveIndexFieldsService) {
        this.retrieveIndexFieldsService = retrieveIndexFieldsService;
    }

    @Override
    public Set<String> getParametricFields(final ResourceIdentifier index) throws HodErrorException {
        final RetrieveIndexFieldsRequestBuilder fieldsParams = new RetrieveIndexFieldsRequestBuilder()
                .setFieldType(FieldType.parametric);

        final RetrieveIndexFieldsResponse indexFields = retrieveIndexFieldsService.retrieveIndexFields(index, fieldsParams);
        return new HashSet<>(indexFields.getAllFields());
    }

}
