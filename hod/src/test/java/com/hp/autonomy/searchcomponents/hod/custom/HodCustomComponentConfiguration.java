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
