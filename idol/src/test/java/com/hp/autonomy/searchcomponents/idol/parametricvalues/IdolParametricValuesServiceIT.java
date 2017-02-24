/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AbstractParametricValuesServiceIT;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolParametricValuesServiceIT extends AbstractParametricValuesServiceIT<IdolParametricRequest, IdolFieldsRequest, IdolFieldsRequestBuilder, IdolQueryRestrictions, String, AciErrorException> {
    @Autowired
    private ObjectFactory<IdolQueryRestrictionsBuilder> queryRestrictionsBuilderFactory;

    @Override
    protected IdolFieldsRequestBuilder fieldsRequestParams(final IdolFieldsRequestBuilder fieldsRequestBuilder) {
        return fieldsRequestBuilder;
    }

    @Override
    protected FieldPath determinePaginatableField() {
        final Map<TagName, ValueDetails> valueDetails = parametricValuesService.getValueDetails(createParametricRequest());

        return valueDetails.entrySet().stream()
                .filter(entry -> entry.getValue().getTotalValues() >= 2)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Pagination test requires a parametric field with more than 2 values"))
                .getKey()
                .getId();
    }

    @Override
    protected IdolParametricRequest noResultsParametricRequest() {
        final IdolQueryRestrictions queryRestrictions = queryRestrictionsBuilderFactory.getObject()
                // No documents will match this text (probably)
                .queryText("sfoiewsfoseinf")
                .build();

        return parametricRequestBuilderFactory.getObject()
                .queryRestrictions(queryRestrictions)
                .fieldName(tagNameFactory.getFieldPath(ParametricValuesService.AUTN_DATE_FIELD))
                .build();
    }
}
