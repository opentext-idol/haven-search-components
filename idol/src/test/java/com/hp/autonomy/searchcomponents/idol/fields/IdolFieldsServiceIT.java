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

package com.hp.autonomy.searchcomponents.idol.fields;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldsServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolFieldsServiceIT extends AbstractFieldsServiceIT<IdolFieldsRequest, IdolFieldsRequestBuilder, IdolQueryRestrictions, AciErrorException> {
    @Override
    protected IdolFieldsRequest createFieldsRequest() {
        return fieldRequestBuilderFactory.getObject()
                .fieldType(FieldTypeParam.Parametric)
                .maxValues(null)
                .build();
    }
}
