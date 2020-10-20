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

package com.hp.autonomy.searchcomponents.hod.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.core.search.fields.AbstractDocumentFieldsService;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

import static com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService.DOCUMENT_FIELDS_SERVICE_BEAN_NAME;

@Component(DOCUMENT_FIELDS_SERVICE_BEAN_NAME)
class HodDocumentFieldsService extends AbstractDocumentFieldsService implements DocumentFieldsService {
    @SuppressWarnings("TypeMayBeWeakened")
    @Autowired
    HodDocumentFieldsService(final ConfigService<? extends HavenSearchCapable> configService) {
        super(configService);
    }

    @SuppressWarnings("CastToConcreteClass")
    @Override
    public Collection<FieldInfo<?>> getHardCodedFields() {
        return Collections.emptyList();
    }
}
