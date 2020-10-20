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

package com.hp.autonomy.searchcomponents.hod.requests;

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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static com.hp.autonomy.searchcomponents.core.databases.DatabasesService.DATABASES_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.fields.FieldsService.FIELDS_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.PARAMETRIC_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.DocumentsService.*;
import static com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndexBuilder.GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.QueryRestrictions.QUERY_RESTRICTIONS_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService.RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.VIEW_REQUEST_BUILDER_BEAN_NAME;

/**
 * Exposes builders for requests as prototype beans.
 * Where a request object is very similar (or identical) between Hod and Hod service implementations, it is convenient
 * to be able to interact with a wired builder interface and avoid constantly interacting with implementation-specific constructors.
 * All builders can be substituted out by supplying an alternative bean.
 */
@Configuration
public class HodRequestBuilderConfiguration {
    @Bean(name = GET_CONTENT_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = GET_CONTENT_REQUEST_BUILDER_BEAN_NAME)
    public HodGetContentRequestBuilder getContentRequestBuilder() {
        return HodGetContentRequestImpl.builder();
    }

    @Bean(name = QUERY_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = QUERY_REQUEST_BUILDER_BEAN_NAME)
    public HodQueryRequestBuilder queryRequestBuilder() {
        return HodQueryRequestImpl.builder();
    }

    @Bean(name = SUGGEST_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = SUGGEST_REQUEST_BUILDER_BEAN_NAME)
    public HodSuggestRequestBuilder suggestRequestBuilder() {
        return HodSuggestRequestImpl.builder();
    }

    @Bean(name = DATABASES_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = DATABASES_REQUEST_BUILDER_BEAN_NAME)
    public HodDatabasesRequestBuilder databasesRequestBuilder() {
        return HodDatabasesRequestImpl.builder();
    }

    @Bean(name = FIELDS_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = FIELDS_REQUEST_BUILDER_BEAN_NAME)
    public HodFieldsRequestBuilder fieldsRequestBuilder() {
        return HodFieldsRequestImpl.builder();
    }

    @Bean(name = PARAMETRIC_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = PARAMETRIC_REQUEST_BUILDER_BEAN_NAME)
    public HodParametricRequestBuilder parametricRequestBuilder() {
        return HodParametricRequestImpl.builder();
    }

    @Bean(name = RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = RELATED_CONCEPTS_REQUEST_BUILDER_BEAN_NAME)
    public HodRelatedConceptsRequestBuilder relatedConceptsRequestBuilder() {
        return HodRelatedConceptsRequestImpl.builder();
    }

    @Bean(name = QUERY_RESTRICTIONS_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = QUERY_RESTRICTIONS_BUILDER_BEAN_NAME)
    public HodQueryRestrictionsBuilder queryRestrictionsBuilder() {
        return HodQueryRestrictionsImpl.builder();
    }

    @Bean(name = GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = GET_CONTENT_REQUEST_INDEX_BUILDER_BEAN_NAME)
    public HodGetContentRequestIndexBuilder getContentRequestIndexBuilder() {
        return HodGetContentRequestIndexImpl.builder();
    }

    @Bean(name = VIEW_REQUEST_BUILDER_BEAN_NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = VIEW_REQUEST_BUILDER_BEAN_NAME)
    public HodViewRequestBuilder viewRequestBuilder() {
        return HodViewRequestImpl.builder();
    }
}
