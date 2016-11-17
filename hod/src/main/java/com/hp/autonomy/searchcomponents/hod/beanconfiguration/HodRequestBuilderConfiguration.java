/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.beanconfiguration;

import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesRequest;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequest;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodRelatedConceptsRequest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Exposes builders for requests as prototype beans.
 * Where a request object is very similar (or identical) between Hod and Hod service implementations, it is convenient
 * to be able to interact with a wired builder interface and avoid constantly interacting with implementation-specific constructors.
 * All builders can be substituted out by supplying an alternative bean.
 */
@Configuration
public class HodRequestBuilderConfiguration {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(GetContentRequest.GetContentRequestBuilder.class)
    public GetContentRequest.GetContentRequestBuilder<ResourceIdentifier> getContentRequestBuilder() {
        return GetContentRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(QueryRequest.QueryRequestBuilder.class)
    public QueryRequest.QueryRequestBuilder<ResourceIdentifier> queryRequestBuilder() {
        return QueryRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(SuggestRequest.SuggestRequestBuilder.class)
    public SuggestRequest.SuggestRequestBuilder<ResourceIdentifier> suggestRequestBuilder() {
        return SuggestRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(DatabasesRequest.DatabasesRequestBuilder.class)
    public HodDatabasesRequest.HodDatabasesRequestBuilder databasesRequestBuilder() {
        return HodDatabasesRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(FieldsRequest.FieldsRequestBuilder.class)
    public HodFieldsRequest.HodFieldsRequestBuilder fieldsRequestBuilder() {
        return HodFieldsRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(ParametricRequest.ParametricRequestBuilder.class)
    public HodParametricRequest.HodParametricRequestBuilder parametricRequestBuilder() {
        return HodParametricRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(RelatedConceptsRequest.RelatedConceptsRequestBuilder.class)
    public HodRelatedConceptsRequest.HodRelatedConceptsRequestBuilder relatedConceptsRequestBuilder() {
        return HodRelatedConceptsRequest.builder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(QueryRestrictions.QueryRestrictionsBuilder.class)
    public HodQueryRestrictions.HodQueryRestrictionsBuilder queryRestrictionsBuilder() {
        return HodQueryRestrictions.builder();
    }
}
