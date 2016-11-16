/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.custom;

import com.hp.autonomy.hod.client.api.analysis.autocomplete.AutocompleteService;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentService;
import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Document;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsService;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;
import static org.mockito.Mockito.mock;

@Configuration
@ConditionalOnProperty(CUSTOMISATION_TEST_ID)
class HodCustomConfiguration {
    @Bean
    public AuthenticationInformationRetriever<HodAuthentication<EntityType.Combined>, HodAuthenticationPrincipal> authenticationInformationRetriever() {
        @SuppressWarnings("unchecked")
        final AuthenticationInformationRetriever<HodAuthentication<EntityType.Combined>, HodAuthenticationPrincipal> authenticationInformationRetriever = mock(AuthenticationInformationRetriever.class);
        return authenticationInformationRetriever;
    }

    @Bean
    public GetContentService<HodSearchResult> getContentService() {
        @SuppressWarnings("unchecked")
        final GetContentService<HodSearchResult> getContentService = mock(GetContentService.class);
        return getContentService;
    }

    @Bean
    public GetContentService<Document> viewGetContentService() {
        @SuppressWarnings("unchecked")
        final GetContentService<Document> viewGetContentService = mock(GetContentService.class);
        return viewGetContentService;
    }

    @Bean
    public QueryTextIndexService<HodSearchResult> queryTextIndexService() {
        @SuppressWarnings("unchecked")
        final QueryTextIndexService<HodSearchResult> queryTextIndexService = mock(QueryTextIndexService.class);
        return queryTextIndexService;
    }

    @Bean
    public QueryTextIndexService<Document> documentQueryTextIndexService() {
        @SuppressWarnings("unchecked")
        final QueryTextIndexService<Document> documentQueryTextIndexService = mock(QueryTextIndexService.class);
        return documentQueryTextIndexService;
    }

    @Bean
    public FindSimilarService<HodSearchResult> findSimilarService() {
        @SuppressWarnings("unchecked")
        final FindSimilarService<HodSearchResult> findSimilarService = mock(FindSimilarService.class);
        return findSimilarService;
    }

    @Bean
    public GetParametricValuesService getParametricValuesService() {
        return mock(GetParametricValuesService.class);
    }


    @Bean
    public ViewDocumentService viewDocumentService() {
        return mock(ViewDocumentService.class);
    }

    @Bean
    public RetrieveIndexFieldsService retrieveIndexFieldsService() {
        return mock(RetrieveIndexFieldsService.class);
    }

    @Bean
    public FindRelatedConceptsService relatedConceptsService() {
        return mock(FindRelatedConceptsService.class);
    }

    @Bean
    public AutocompleteService autocompleteService() {
        return mock(AutocompleteService.class);
    }

    @Bean
    public ResourcesService resourcesService() {
        return mock(ResourcesService.class);
    }
}
