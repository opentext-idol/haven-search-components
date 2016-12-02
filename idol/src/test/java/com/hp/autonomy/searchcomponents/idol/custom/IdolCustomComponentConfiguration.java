/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.custom;

import com.hp.autonomy.frontend.configuration.validation.Validator;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfig;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandlerProxy;
import com.hp.autonomy.searchcomponents.idol.search.QueryExecutor;
import com.hp.autonomy.searchcomponents.idol.search.QueryResponseParser;
import com.hp.autonomy.searchcomponents.idol.search.fields.FieldsParser;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService.DOCUMENT_FIELDS_SERVICE_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;
import static com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever.ACI_SERVICE_RETRIEVER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable.ANSWER_SERVER_VALIDATOR_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable.QUERY_MANIPULATION_VALIDATOR_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.search.QueryExecutor.QUERY_EXECUTOR_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.search.QueryResponseParser.QUERY_RESPONSE_PARSER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.search.fields.FieldsParser.FIELDS_PARSER_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable.VIEW_CONFIG_VALIDATOR_BEAN_NAME;
import static org.mockito.Mockito.mock;

@SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
@Configuration
@ConditionalOnProperty(CUSTOMISATION_TEST_ID)
public class IdolCustomComponentConfiguration {
    @Bean
    @Primary
    public DocumentFieldsService customDocumentFieldsService(@Qualifier(DOCUMENT_FIELDS_SERVICE_BEAN_NAME) final DocumentFieldsService documentFieldsService) {
        return documentFieldsService;
    }

    @Bean
    @Primary
    public AciServiceRetriever customAciServiceRetriever(@Qualifier(ACI_SERVICE_RETRIEVER_BEAN_NAME) final AciServiceRetriever aciServiceRetriever) {
        return aciServiceRetriever;
    }

    @Bean(name = QUERY_MANIPULATION_VALIDATOR_BEAN_NAME)
    public Validator<QueryManipulation> customQueryManipulationValidator() {
        @SuppressWarnings("unchecked")
        final Validator<QueryManipulation> validator = mock(Validator.class);
        return validator;
    }

    @Bean(name = ANSWER_SERVER_VALIDATOR_BEAN_NAME)
    public Validator<AnswerServerConfig> customAnswerServerValidator() {
        @SuppressWarnings("unchecked")
        final Validator<AnswerServerConfig> validator = mock(Validator.class);
        return validator;
    }

    @Bean(name = VIEW_CONFIG_VALIDATOR_BEAN_NAME)
    public Validator<ViewConfig> customViewConfigValidator() {
        @SuppressWarnings("unchecked")
        final Validator<ViewConfig> validator = mock(Validator.class);
        return validator;
    }

    @Bean
    @Primary
    public QueryResponseParser customQueryResponseParser(@Qualifier(QUERY_RESPONSE_PARSER_BEAN_NAME) final QueryResponseParser queryResponseParser) {
        return queryResponseParser;
    }

    @Bean
    @Primary
    public QueryExecutor customQueryExecutor(@Qualifier(QUERY_EXECUTOR_BEAN_NAME) final QueryExecutor queryExecutor) {
        return queryExecutor;
    }

    @Bean
    @Primary
    public HavenSearchAciParameterHandler customHavenSearchAciParameterHandler() {
        return new HavenSearchAciParameterHandlerProxy() {
        };
    }

    @Bean
    @Primary
    public FieldsParser customFieldsParser(@Qualifier(FIELDS_PARSER_BEAN_NAME) final FieldsParser fieldsParser) {
        return fieldsParser;
    }
}
