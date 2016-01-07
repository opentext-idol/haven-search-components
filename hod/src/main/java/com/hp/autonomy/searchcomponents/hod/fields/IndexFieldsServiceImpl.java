/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.fields.FieldType;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsResponse;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@ConditionalOnMissingBean(IndexFieldsService.class)
@Service
public class IndexFieldsServiceImpl implements IndexFieldsService {

    private final RetrieveIndexFieldsService retrieveIndexFieldsService;

    @Autowired
    public IndexFieldsServiceImpl(final RetrieveIndexFieldsService retrieveIndexFieldsService) {
        this.retrieveIndexFieldsService = retrieveIndexFieldsService;
    }

    @Override
    public Set<String> getParametricFields(final ResourceIdentifier index) throws HodErrorException {
        return getParametricFields(null, index);
    }

    @Override
    public Set<String> getParametricFields(final TokenProxy<?, TokenType.Simple> tokenProxy, final ResourceIdentifier index) throws HodErrorException {
        final RetrieveIndexFieldsRequestBuilder fieldsParams = new RetrieveIndexFieldsRequestBuilder()
            .setFieldType(FieldType.parametric);

        final RetrieveIndexFieldsResponse indexFields = tokenProxy == null ? retrieveIndexFieldsService.retrieveIndexFields(index, fieldsParams) : retrieveIndexFieldsService.retrieveIndexFields(tokenProxy, index, fieldsParams);

        return new HashSet<>(indexFields.getAllFields());
    }

}
