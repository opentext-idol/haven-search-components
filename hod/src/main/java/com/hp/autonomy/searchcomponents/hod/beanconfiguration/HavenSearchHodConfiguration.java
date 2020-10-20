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
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricRangesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricRangesServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesServiceImpl;
import com.hp.autonomy.hod.client.api.textindex.query.search.*;
import com.hp.autonomy.hod.client.api.textindex.status.TextIndexStatusService;
import com.hp.autonomy.hod.client.api.textindex.status.TextIndexStatusServiceImpl;
import com.hp.autonomy.hod.client.config.HodServiceConfig;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Defines Spring beans required for using this module
 */
@SuppressWarnings("WeakerAccess")
@Configuration
@ComponentScan({"com.hp.autonomy.searchcomponents.core", "com.hp.autonomy.searchcomponents.hod"})
public class HavenSearchHodConfiguration {
    /**
     * The bean name of the HoD client service used for retrieving document content.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String GET_CONTENT_SERVICE_BEAN_NAME = "getContentService";

    /**
     * The bean name of the HoD client service used for retrieving the document reference during a view action.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String VIEW_GET_CONTENT_SERVICE_BEAN_NAME = "viewGetContentService";

    /**
     * The bean name of the HoD client service used for performing standard queries.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String QUERY_TEXT_INDEX_SERVICE_BEAN_NAME = "queryTextIndexService";

    /**
     * The bean name of the HoD client service used for performing suggest queries.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String FIND_SIMILAR_SERVICE_BEAN_NAME = "findSimilarService";

    /**
     * The bean name of the HoD client service used for viewing a static content promotion.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String DOCUMENT_QUERY_TEXT_INDEX_SERVICE_BEAN_NAME = "documentQueryTextIndexService";

    @Autowired
    private HodServiceConfig<EntityType.Combined, TokenType.Simple> hodServiceConfig;

    @Bean
    @ConditionalOnMissingBean(name = GET_CONTENT_SERVICE_BEAN_NAME)
    public GetContentService<HodSearchResult> getContentService() {
        return new GetContentServiceImpl<>(hodServiceConfig, HodSearchResult.class);
    }

    @Bean
    @ConditionalOnMissingBean(name = VIEW_GET_CONTENT_SERVICE_BEAN_NAME)
    public GetContentService<Document> viewGetContentService() {
        return new GetContentServiceImpl<>(hodServiceConfig, Document.class);
    }

    @Bean
    @ConditionalOnMissingBean(name = QUERY_TEXT_INDEX_SERVICE_BEAN_NAME)
    public QueryTextIndexService<HodSearchResult> queryTextIndexService() {
        return new QueryTextIndexServiceImpl<>(hodServiceConfig, HodSearchResult.class);
    }

    @Bean
    @ConditionalOnMissingBean(name = DOCUMENT_QUERY_TEXT_INDEX_SERVICE_BEAN_NAME)
    public QueryTextIndexService<Document> documentQueryTextIndexService() {
        return QueryTextIndexServiceImpl.documentsService(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(name = FIND_SIMILAR_SERVICE_BEAN_NAME)
    public FindSimilarService<HodSearchResult> findSimilarService() {
        return new FindSimilarServiceImpl<>(hodServiceConfig, HodSearchResult.class);
    }

    @Bean
    @ConditionalOnMissingBean(GetParametricValuesService.class)
    public GetParametricValuesService getParametricValuesService() {
        return new GetParametricValuesServiceImpl(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(GetParametricRangesService.class)
    public GetParametricRangesService getParametricRangesService() {
        return new GetParametricRangesServiceImpl(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(ViewDocumentService.class)
    public ViewDocumentService viewDocumentService() {
        return new ViewDocumentServiceImpl(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(RetrieveIndexFieldsService.class)
    public RetrieveIndexFieldsService retrieveIndexFieldsService() {
        return new RetrieveIndexFieldsServiceImpl(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(FindRelatedConceptsService.class)
    public FindRelatedConceptsService findRelatedConceptsService() {
        return new FindRelatedConceptsServiceImpl(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(AutocompleteService.class)
    public AutocompleteService autocompleteService(final HodServiceConfig<EntityType.Combined, TokenType.Simple> hodServiceConfig) {
        return new AutocompleteServiceImpl(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(ResourcesService.class)
    public ResourcesService resourcesService() {
        return new ResourcesServiceImpl(hodServiceConfig);
    }

    @Bean
    @ConditionalOnMissingBean(TextIndexStatusService.class)
    public TextIndexStatusService textIndexStatusService(final HodServiceConfig<EntityType.Combined, TokenType.Simple> hodServiceConfig) {
        return new TextIndexStatusServiceImpl(hodServiceConfig);
    }
}
