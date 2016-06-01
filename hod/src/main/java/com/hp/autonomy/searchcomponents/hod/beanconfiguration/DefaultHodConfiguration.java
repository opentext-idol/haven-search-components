/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.beanconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.authentication.SpringSecurityAuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AdaptiveBucketSizeEvaluatorFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.databases.Database;
import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesRequest;
import com.hp.autonomy.searchcomponents.hod.databases.HodDatabasesService;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.searchcomponents.hod.languages.HodLanguagesService;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequest;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.search.HodDocumentsService;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import com.hp.autonomy.searchcomponents.hod.search.fields.HodSearchResultDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DefaultHodConfiguration {
    @Bean
    public ObjectMapper hodSearchResultObjectMapper(final HodSearchResultDeserializer searchResultDeserializer) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule customModule = new CustomModule();
        customModule.addDeserializer(HodSearchResult.class, searchResultDeserializer);
        objectMapper.registerModule(customModule);

        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean(LanguagesService.class)
    public LanguagesService languagesService() {
        return new HodLanguagesService();
    }

    @Bean
    @ConditionalOnMissingBean(DatabasesService.class)
    public DatabasesService<Database, HodDatabasesRequest, HodErrorException> databasesService(
            final ResourcesService resourcesService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever
    ) {
        return new HodDatabasesService(resourcesService, authenticationInformationRetriever);
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    @Bean
    @ConditionalOnMissingBean(DocumentsService.class)
    public DocumentsService<ResourceIdentifier, HodSearchResult, HodErrorException> documentsService(
            final FindSimilarService<HodSearchResult> findSimilarService,
            final ConfigService<? extends HodSearchCapable> configService,
            final QueryTextIndexService<HodSearchResult> queryTextIndexService,
            final GetContentService<HodSearchResult> getContentService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever,
            final DocumentFieldsService documentFieldsService
    ) {
        return new HodDocumentsService(findSimilarService, configService, queryTextIndexService, getContentService, authenticationInformationRetriever, documentFieldsService);
    }

    @Bean
    @ConditionalOnMissingBean(ParametricValuesService.class)
    public ParametricValuesService<HodParametricRequest, ResourceIdentifier, HodErrorException> parametricValuesService(
            final FieldsService<HodFieldsRequest, HodErrorException> fieldsService,
            final GetParametricValuesService getParametricValuesService,
            final ConfigService<? extends HodSearchCapable> configService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever,
            final AdaptiveBucketSizeEvaluatorFactory bucketSizeEvaluatorFactory
    ) {
        return new HodParametricValuesService(fieldsService, getParametricValuesService, configService, authenticationInformationRetriever, bucketSizeEvaluatorFactory);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationInformationRetriever.class)
    public AuthenticationInformationRetriever<HodAuthentication<EntityType.Combined>, HodAuthenticationPrincipal> authenticationInformationRetriever() {
        return new SpringSecurityAuthenticationInformationRetriever<>();
    }

    private static class CustomModule extends SimpleModule {
        private static final long serialVersionUID = -7185088412606149305L;
    }
}
