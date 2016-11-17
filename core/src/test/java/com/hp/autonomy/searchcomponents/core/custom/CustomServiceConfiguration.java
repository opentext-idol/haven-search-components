/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.custom;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;
import com.hp.autonomy.types.IdolDatabase;
import com.hp.autonomy.types.requests.idol.actions.query.QuerySummaryElement;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.Serializable;

import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;

@SuppressWarnings({"AnonymousInnerClassWithTooManyMethods", "SpringJavaAutowiringInspection"})
@Configuration
@ConditionalOnProperty(CUSTOMISATION_TEST_ID)
public class CustomServiceConfiguration {
    @Bean
    @Primary
    public <D extends IdolDatabase, R extends DatabasesRequest, E extends Exception> DatabasesService<D, R, E> customDatabaseService(
            @Qualifier(DatabasesService.DATABASES_SERVICE_BEAN_NAME) final DatabasesService<D, R, E> databasesService) {
        return databasesService;
    }

    @Bean
    @Primary
    public <R extends FieldsRequest, E extends Exception> FieldsService<R, E> customFieldsService(
            @Qualifier(FieldsService.FIELD_SERVICE_BEAN_NAME) final FieldsService<R, E> fieldsService) {
        return fieldsService;
    }

    @Bean
    @Primary
    public LanguagesService customLanguagesService(
            @Qualifier(LanguagesService.LANGUAGES_SERVICE_BEAN_NAME) final LanguagesService languagesService) {
        return languagesService;
    }

    @Bean
    @Primary
    public <R extends ParametricRequest<S>, S extends Serializable, E extends Exception> ParametricValuesService<R, S, E> customParametricValuesService(
            @Qualifier(ParametricValuesService.PARAMETRIC_VALUES_SERVICE_BEAN_NAME) final ParametricValuesService<R, S, E> parametricValuesService) {
        return parametricValuesService;
    }

    @Bean
    @Primary
    public <S extends Serializable, D extends SearchResult, E extends Exception> DocumentsService<S, D, E> customDocumentsService(
            @Qualifier(DocumentsService.DOCUMENTS_SERVICE_BEAN_NAME) final DocumentsService<S, D, E> documentsService) {
        return documentsService;
    }

    @Bean
    @Primary
    public <Q extends QuerySummaryElement, S extends Serializable, E extends Exception> RelatedConceptsService<Q, S, E> customRelatedConceptsService(
            @Qualifier(RelatedConceptsService.RELATED_CONCEPTS_SERVICE_BEAN_NAME) final RelatedConceptsService<Q, S, E> relatedConceptsService) {
        return relatedConceptsService;
    }

    @Bean
    @Primary
    public <E extends Exception> TypeAheadService<E> customTypeAheadService(
            @Qualifier(TypeAheadService.TYPE_AHEAD_SERVICE_BEAN_NAME) final TypeAheadService<E> typeAheadService) {
        return typeAheadService;
    }

    @Bean
    @Primary
    public <S extends Serializable, E extends Exception> ViewServerService<S, E> customViewServerService(
            @Qualifier(ViewServerService.VIEW_SERVER_SERVICE_BEAN_NAME) final ViewServerService<S, E> viewServerService) {
        return viewServerService;
    }
}
