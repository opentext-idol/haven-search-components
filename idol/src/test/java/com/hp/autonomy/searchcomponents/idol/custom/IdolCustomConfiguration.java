/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.custom;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;
import static com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration.ACI_SERVICE_BEAN_NAME;
import static org.mockito.Mockito.mock;

@SuppressWarnings("UnusedParameters")
@Configuration
@ConditionalOnProperty(CUSTOMISATION_TEST_ID)
class IdolCustomConfiguration {
    @Bean
    public AuthenticationInformationRetriever<UsernamePasswordAuthenticationToken, CommunityPrincipal> authenticationInformationRetriever() {
        @SuppressWarnings("unchecked")
        final AuthenticationInformationRetriever<UsernamePasswordAuthenticationToken, CommunityPrincipal> authenticationInformationRetriever = mock(AuthenticationInformationRetriever.class);
        return authenticationInformationRetriever;
    }

    @Bean
    public IdolAnnotationsProcessorFactory idolAnnotationsProcessorFactory() {
        return mock(IdolAnnotationsProcessorFactory.class);
    }

    @Bean
    public AciService contentAciService(@Qualifier(ACI_SERVICE_BEAN_NAME) final AciService aciService, final ConfigService<IdolSearchCapable> configService) {
        return mock(AciService.class);
    }


    @Bean
    public AciService qmsAciService(@Qualifier(ACI_SERVICE_BEAN_NAME) final AciService aciService, final ConfigService<IdolSearchCapable> configService) {
        return mock(AciService.class);
    }

    @Bean
    public AciService viewAciService(@Qualifier(ACI_SERVICE_BEAN_NAME) final AciService aciService, final ConfigService<IdolSearchCapable> configService) {
        return mock(AciService.class);
    }

    @Bean
    public AciService answerServerAciService(@Qualifier(ACI_SERVICE_BEAN_NAME) final AciService aciService, final ConfigService<IdolSearchCapable> configService) {
        return mock(AciService.class);
    }

    @Bean
    public AciService aciService(final HttpClient httpClient) {
        return mock(AciService.class);
    }

    @Bean
    public AciService validatorAciService(final HttpClient validatorHttpClient) {
        return mock(AciService.class);
    }

    @Bean
    public HttpClient httpClient() {
        return mock(HttpClient.class);
    }

    @Bean
    public HttpClient validatorHttpClient() {
        return mock(HttpClient.class);
    }
}
