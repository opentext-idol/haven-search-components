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

package com.hp.autonomy.searchcomponents.idol.custom;

import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.parametricvalues.IdolParametricRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolGetContentRequestIndexBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolRelatedConceptsRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolSuggestRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequestBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import static com.hp.autonomy.searchcomponents.core.databases.DatabasesService.DATABASES_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.fields.FieldsService.FIELDS_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.PARAMETRIC_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.DocumentsService.*;
import static com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndexBuilder.GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.QueryRestrictions.QUERY_RESTRICTIONS_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService.RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;
import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.VIEW_REQUEST_BUILDER_BEAN_NAME;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@ConditionalOnProperty(CUSTOMISATION_TEST_ID)
public class IdolCustomRequestBuilderConfiguration {
    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolGetContentRequestBuilder customGetContentRequestBuilder(
            @Qualifier(GET_CONTENT_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolGetContentRequestBuilder> getContentRequestBuilderFactory) {
        return getContentRequestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolQueryRequestBuilder customQueryRequestBuilder(
            @Qualifier(QUERY_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolQueryRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolSuggestRequestBuilder customSuggestRequestBuilder(
            @Qualifier(SUGGEST_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolSuggestRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolDatabasesRequestBuilder customDatabasesRequestBuilder(
            @Qualifier(DATABASES_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolDatabasesRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolFieldsRequestBuilder customFieldsRequestBuilder(
            @Qualifier(FIELDS_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolFieldsRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolParametricRequestBuilder customParametricRequestBuilder(
            @Qualifier(PARAMETRIC_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolParametricRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolRelatedConceptsRequestBuilder customRelatedConceptsRequestBuilder(
            @Qualifier(RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolRelatedConceptsRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolQueryRestrictionsBuilder customQueryRestrictionsBuilder(
            @Qualifier(QUERY_RESTRICTIONS_BUILDER_BEAN_NAME) final ObjectFactory<IdolQueryRestrictionsBuilder> queryRestrictionsBuilderFactory
    ) {
        return queryRestrictionsBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolGetContentRequestIndexBuilder getContentRequestIndexBuilder(
            @Qualifier(GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME) final ObjectFactory<IdolGetContentRequestIndexBuilder> getContentRequestIndexBuilderFactory
    ) {
        return getContentRequestIndexBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdolViewRequestBuilder customViewRequestBuilder(
            @Qualifier(VIEW_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<IdolViewRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }
}
