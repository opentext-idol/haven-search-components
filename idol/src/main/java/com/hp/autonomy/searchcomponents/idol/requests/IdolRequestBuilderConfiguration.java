/*
 * Copyright 2015-2017 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static com.hp.autonomy.searchcomponents.core.databases.DatabasesService.DATABASES_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.fields.FieldsService.FIELDS_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.PARAMETRIC_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.DocumentsService.GET_CONTENT_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.DocumentsService.QUERY_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.DocumentsService.SUGGEST_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndexBuilder.GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.QueryRestrictions.QUERY_RESTRICTIONS_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService.RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.VIEW_REQUEST_BUILDER_BEAN_NAME;

/**
 * Exposes builders for requests as prototype beans.
 * Where a request object is very similar (or identical) between Idol and Idol service implementations, it is convenient
 * to be able to interact with a wired builder interface and avoid constantly interacting with implementation-specific constructors.
 * All builders can be substituted out by supplying an alternative bean.
 */
@Configuration
public class IdolRequestBuilderConfiguration {
    @Bean(name = GET_CONTENT_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = GET_CONTENT_REQUEST_BUILDER_BEAN_NAME)
    public IdolGetContentRequestBuilder getContentRequestBuilder() {
        return IdolGetContentRequestImpl.builder();
    }

    @Bean(name = QUERY_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = QUERY_REQUEST_BUILDER_BEAN_NAME)
    public IdolQueryRequestBuilder queryRequestBuilder() {
        return IdolQueryRequestImpl.builder();
    }

    @Bean(name = SUGGEST_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = SUGGEST_REQUEST_BUILDER_BEAN_NAME)
    public IdolSuggestRequestBuilder suggestRequestBuilder() {
        return IdolSuggestRequestImpl.builder();
    }

    @Bean(name = DATABASES_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = DATABASES_REQUEST_BUILDER_BEAN_NAME)
    public IdolDatabasesRequestBuilder databasesRequestBuilder() {
        return IdolDatabasesRequestImpl.builder();
    }

    @Bean(name = FIELDS_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = FIELDS_REQUEST_BUILDER_BEAN_NAME)
    public IdolFieldsRequestBuilder fieldsRequestBuilder() {
        return IdolFieldsRequestImpl.builder();
    }

    @Bean(name = PARAMETRIC_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = PARAMETRIC_REQUEST_BUILDER_BEAN_NAME)
    public IdolParametricRequestBuilder parametricRequestBuilder() {
        return IdolParametricRequestImpl.builder();
    }

    @Bean(name = RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME)
    public IdolRelatedConceptsRequestBuilder relatedConceptsRequestBuilder() {
        return IdolRelatedConceptsRequestImpl.builder();
    }

    @Bean(name = QUERY_RESTRICTIONS_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = QUERY_RESTRICTIONS_BUILDER_BEAN_NAME)
    public IdolQueryRestrictionsBuilder queryRestrictionsBuilder() {
        return IdolQueryRestrictionsImpl.builder();
    }

    @Bean(name = GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME)
    public IdolGetContentRequestIndexBuilder getContentRequestIndexBuilder() {
        return IdolGetContentRequestIndexImpl.builder();
    }

    @Bean(name = VIEW_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = VIEW_REQUEST_BUILDER_BEAN_NAME)
    public IdolViewRequestBuilder viewRequestBuilder() {
        return IdolViewRequestImpl.builder();
    }
}
