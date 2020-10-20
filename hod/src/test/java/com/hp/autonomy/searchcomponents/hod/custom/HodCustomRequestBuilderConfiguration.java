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

import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodGetContentRequestIndexBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictionsBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodRelatedConceptsRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.search.HodSuggestRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.view.HodViewRequestBuilder;
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
public class HodCustomRequestBuilderConfiguration {
    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodGetContentRequestBuilder customGetContentRequestBuilder(
            @Qualifier(GET_CONTENT_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodGetContentRequestBuilder> getContentRequestBuilderFactory) {
        return getContentRequestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodQueryRequestBuilder customQueryRequestBuilder(
            @Qualifier(QUERY_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodQueryRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodSuggestRequestBuilder customSuggestRequestBuilder(
            @Qualifier(SUGGEST_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodSuggestRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodDatabasesRequestBuilder customDatabasesRequestBuilder(
            @Qualifier(DATABASES_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodDatabasesRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodFieldsRequestBuilder customFieldsRequestBuilder(
            @Qualifier(FIELDS_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodFieldsRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodParametricRequestBuilder customParametricRequestBuilder(
            @Qualifier(PARAMETRIC_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodParametricRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodRelatedConceptsRequestBuilder customRelatedConceptsRequestBuilder(
            @Qualifier(RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodRelatedConceptsRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodQueryRestrictionsBuilder customQueryRestrictionsBuilder(
            @Qualifier(QUERY_RESTRICTIONS_BUILDER_BEAN_NAME) final ObjectFactory<HodQueryRestrictionsBuilder> queryRestrictionsBuilderFactory
    ) {
        return queryRestrictionsBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodGetContentRequestIndexBuilder getContentRequestIndexBuilder(
            @Qualifier(GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME) final ObjectFactory<HodGetContentRequestIndexBuilder> getContentRequestIndexBuilderFactory
    ) {
        return getContentRequestIndexBuilderFactory.getObject();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HodViewRequestBuilder customViewRequestBuilder(
            @Qualifier(VIEW_REQUEST_BUILDER_BEAN_NAME) final ObjectFactory<HodViewRequestBuilder> requestBuilderFactory
    ) {
        return requestBuilderFactory.getObject();
    }
}
