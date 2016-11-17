/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequest;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.parametricvalues.IdolParametricRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolRelatedConceptsRequest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Exposes builders for requests as prototype beans.
 * Where a request object is very similar (or identical) between Idol and Hod service implementations, it is convenient
 * to be able to interact with a wired builder interface and avoid constantly interacting with implementation-specific constructors.
 * All builders can be substituted out by supplying an alternative bean.
 */
@Configuration
public class IdolRequestBuilderConfiguration {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(GetContentRequest.GetContentRequestBuilder.class)
    public GetContentRequest.GetContentRequestBuilder<String> getContentRequestBuilder() {
        return GetContentRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(QueryRequest.QueryRequestBuilder.class)
    public QueryRequest.QueryRequestBuilder<String> queryRequestBuilder() {
        return QueryRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(SuggestRequest.SuggestRequestBuilder.class)
    public SuggestRequest.SuggestRequestBuilder<String> suggestRequestBuilder() {
        return SuggestRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(DatabasesRequest.DatabasesRequestBuilder.class)
    public IdolDatabasesRequest.IdolDatabasesRequestBuilder databasesRequestBuilder() {
        return IdolDatabasesRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(FieldsRequest.FieldsRequestBuilder.class)
    public IdolFieldsRequest.IdolFieldsRequestBuilder fieldsRequestBuilder() {
        return IdolFieldsRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(ParametricRequest.ParametricRequestBuilder.class)
    public IdolParametricRequest.IdolParametricRequestBuilder parametricRequestBuilder() {
        return IdolParametricRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(RelatedConceptsRequest.RelatedConceptsRequestBuilder.class)
    public IdolRelatedConceptsRequest.IdolRelatedConceptsRequestBuilder relatedConceptsRequestBuilder() {
        return IdolRelatedConceptsRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(QueryRestrictions.QueryRestrictionsBuilder.class)
    public IdolQueryRestrictions.IdolQueryRestrictionsBuilder queryRestrictionsBuilder() {
        return IdolQueryRestrictions.builder();
    }
}
