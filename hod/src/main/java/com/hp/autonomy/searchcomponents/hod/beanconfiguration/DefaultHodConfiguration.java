/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.beanconfiguration;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.fields.RetrieveIndexFieldsService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.search.FindSimilarService;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationCapable;
import com.hp.autonomy.searchcomponents.hod.databases.ResourceMapper;
import com.hp.autonomy.searchcomponents.hod.databases.ResourceMapperImpl;
import com.hp.autonomy.searchcomponents.hod.fields.IndexFieldsService;
import com.hp.autonomy.searchcomponents.hod.fields.IndexFieldsServiceImpl;
import com.hp.autonomy.searchcomponents.hod.languages.HodLanguagesService;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricRequest;
import com.hp.autonomy.searchcomponents.hod.parametricvalues.HodParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.search.HodDocumentsService;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DefaultHodConfiguration {
    @Bean
    @ConditionalOnMissingBean(LanguagesService.class)
    public LanguagesService languagesService() {
        return new HodLanguagesService();
    }

    @Bean
    @ConditionalOnMissingBean(DocumentsService.class)
    public DocumentsService<ResourceIdentifier, HodSearchResult, HodErrorException> documentsService(final FindSimilarService<HodSearchResult> findSimilarService, final ConfigService<? extends QueryManipulationCapable> configService, final QueryTextIndexService<HodSearchResult> queryTextIndexService) {
        return new HodDocumentsService(findSimilarService, configService, queryTextIndexService);
    }

    @Bean
    @ConditionalOnMissingBean(ParametricValuesService.class)
    public ParametricValuesService<HodParametricRequest, ResourceIdentifier, HodErrorException> parametricValuesService(final GetParametricValuesService getParametricValuesService) {
        return new HodParametricValuesService(getParametricValuesService);
    }

    @Bean
    @ConditionalOnMissingBean(ResourceMapper.class)
    public ResourceMapper resourceMapper(final IndexFieldsService indexFieldsService) {
        return new ResourceMapperImpl(indexFieldsService);
    }

    @Bean
    @ConditionalOnMissingBean(IndexFieldsService.class)
    public IndexFieldsService indexFieldsService(final RetrieveIndexFieldsService retrieveIndexFieldsService) {
        return new IndexFieldsServiceImpl(retrieveIndexFieldsService);
    }
}
