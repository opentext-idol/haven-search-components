/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.custom;

import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService.DOCUMENT_FIELDS_SERVICE_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;

@SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
@Configuration
@ConditionalOnProperty(CUSTOMISATION_TEST_ID)
public class HodCustomComponentConfiguration {
    @Bean
    @Primary
    public DocumentFieldsService customDocumentFieldsService(@Qualifier(DOCUMENT_FIELDS_SERVICE_BEAN_NAME) final DocumentFieldsService documentFieldsService) {
        return documentFieldsService;
    }
}
