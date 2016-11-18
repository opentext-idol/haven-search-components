/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
