/*
 * Copyright 2015 Open Text.
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

package com.hp.autonomy.searchcomponents.core.custom;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsRequest;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.core.view.ViewRequest;
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
    public <R extends ParametricRequest<Q>, Q extends QueryRestrictions<?>, E extends Exception> ParametricValuesService<R, Q, E> customParametricValuesService(
            @Qualifier(ParametricValuesService.PARAMETRIC_VALUES_SERVICE_BEAN_NAME) final ParametricValuesService<R, Q, E> parametricValuesService) {
        return parametricValuesService;
    }

    @Bean
    @Primary
    public <RQ extends QueryRequest<Q>, RS extends SuggestRequest<Q>, RC extends GetContentRequest<?>, Q extends QueryRestrictions<?>, D extends SearchResult, E extends Exception> DocumentsService<RQ, RS, RC, Q, D, E> customDocumentsService(
            @Qualifier(DocumentsService.DOCUMENTS_SERVICE_BEAN_NAME) final DocumentsService<RQ, RS, RC, Q, D, E> documentsService) {
        return documentsService;
    }

    @Bean
    @Primary
    public <R extends RelatedConceptsRequest<Q>, T extends QuerySummaryElement, Q extends QueryRestrictions<?>, E extends Exception> RelatedConceptsService<R, T, Q, E> customRelatedConceptsService(
            @Qualifier(RelatedConceptsService.RELATED_CONCEPTS_SERVICE_BEAN_NAME) final RelatedConceptsService<R, T, Q, E> relatedConceptsService) {
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
    public <R extends ViewRequest<S>, S extends Serializable, E extends Exception> ViewServerService<R, S, E> customViewServerService(
            @Qualifier(ViewServerService.VIEW_SERVER_SERVICE_BEAN_NAME) final ViewServerService<R, S, E> viewServerService) {
        return viewServerService;
    }
}
