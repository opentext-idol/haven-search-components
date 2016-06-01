/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.beanconfiguration;

import com.hp.autonomy.hod.client.api.analysis.autocomplete.AutocompleteService;
import com.hp.autonomy.hod.client.api.analysis.autocomplete.AutocompleteServiceImpl;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentService;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentServiceImpl;
import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.ResourcesService;
import com.hp.autonomy.hod.client.api.resource.ResourcesServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.search.Document;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsService;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindRelatedConceptsServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexServiceImpl;
import com.hp.autonomy.hod.client.config.HodServiceConfig;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Defines Spring beans required for using this module
 */
@Configuration
@ComponentScan({"com.hp.autonomy.searchcomponents.core", "com.hp.autonomy.searchcomponents.hod"})
public class HavenSearchHodConfiguration {
    @Autowired
    private HodServiceConfig<EntityType.Combined, TokenType.Simple> hodServiceConfig;

    @Bean
    public GetContentService<HodSearchResult> getContentService() {
        return new GetContentServiceImpl<>(hodServiceConfig, HodSearchResult.class);
    }

    @Bean
    public GetContentService<Document> viewGetContentService() {
        return new GetContentServiceImpl<>(hodServiceConfig, Document.class);
    }

    @Bean
    public GetParametricValuesService getParametricValuesService() {
        return new GetParametricValuesServiceImpl(hodServiceConfig);
    }

    @Bean
    public QueryTextIndexService<HodSearchResult> queryTextIndexService() {
        return new QueryTextIndexServiceImpl<>(hodServiceConfig, HodSearchResult.class);
    }

    @Bean
    public FindSimilarService<HodSearchResult> findSimilarService() {
        return new FindSimilarServiceImpl<>(hodServiceConfig, HodSearchResult.class);
    }

    @Bean
    public ViewDocumentService viewDocumentService() {
        return new ViewDocumentServiceImpl(hodServiceConfig);
    }

    @Bean
    public QueryTextIndexService<Document> documentQueryTextIndexService() {
        return QueryTextIndexServiceImpl.documentsService(hodServiceConfig);
    }

    @Bean
    public RetrieveIndexFieldsService retrieveIndexFieldsService() {
        return new RetrieveIndexFieldsServiceImpl(hodServiceConfig);
    }

    @Bean
    public FindRelatedConceptsService relatedConceptsService() {
        return new FindRelatedConceptsServiceImpl(hodServiceConfig);
    }

    @Bean
    public AutocompleteService autocompleteService(final HodServiceConfig<EntityType.Combined, TokenType.Simple> hodServiceConfig) {
        return new AutocompleteServiceImpl(hodServiceConfig);
    }

    @Bean
    public ResourcesService resourcesService() {
        return new ResourcesServiceImpl(hodServiceConfig);
    }
}
