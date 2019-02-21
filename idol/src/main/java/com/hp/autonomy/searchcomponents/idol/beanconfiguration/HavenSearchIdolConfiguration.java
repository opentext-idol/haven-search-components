/*
 * Copyright 2015-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.impl.AciServiceImpl;
import com.autonomy.aci.client.transport.AciHttpClient;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.transport.impl.AciHttpClientImpl;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.aci.AbstractConfigurableAciService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfig;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolComponentLabelLookup;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.types.idol.marshalling.Jaxb2ParsingConfiguration;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import com.hpe.bigdata.frontend.spring.authentication.SpringSecurityAuthenticationInformationRetriever;
import org.apache.commons.lang.BooleanUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Defines Spring beans required for using this module
 *
 * @param <C> application config type
 */
@SuppressWarnings("WeakerAccess")
@Configuration
@ComponentScan({"com.hp.autonomy.searchcomponents.core", "com.hp.autonomy.searchcomponents.idol"})
@Import(Jaxb2ParsingConfiguration.class)
public class HavenSearchIdolConfiguration<C extends IdolSearchCapable> {
    /**
     * The bean name of the root {@link AciService} used for all standard interactions with Idol.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String ACI_SERVICE_BEAN_NAME = "aciService";

    /**
     * The bean name of the root {@link AciService} used for queries against Content.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String CONTENT_ACI_SERVICE_BEAN_NAME = "contentAciService";

    /**
     * The bean name of the root {@link AciService} used for queries against QMS.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String QMS_ACI_SERVICE_BEAN_NAME = "qmsAciService";

    /**
     * The bean name of the root {@link AciService} used for queries against ViewServer.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String VIEW_ACI_SERVICE_BEAN_NAME = "viewAciService";

    /**
     * The bean name of the root {@link AciService} used for queries against AnswerServer.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String ANSWER_SERVER_ACI_SERVICE_BEAN_NAME = "answerServerAciService";

    /**
     * The bean name of the root {@link AciService} used for Idol validation checks.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String VALIDATOR_ACI_SERVICE_BEAN_NAME = "validatorAciService";

    /**
     * The bean name of the aci http client used for all standard interactions with Idol.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String ACI_HTTP_CLIENT_BEAN_NAME = "aciHttpClient";

    /**
     * The bean name of the aci http client used for Idol validation checks.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String VALIDATOR_ACI_HTTP_CLIENT_BEAN_NAME = "validatorAciHttpClient";

    /**
     * The bean name of the http client settings used for all standard interactions with Idol.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String HTTP_CLIENT_BEAN_NAME = "httpClient";

    /**
     * The bean name of the http client settings used for Idol validation checks.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    public static final String VALIDATOR_HTTP_CLIENT_BEAN_NAME = "validatorHttpClient";

    private static final int HTTP_SOCKET_TIMEOUT = 180000;
    private static final int HTTP_MAX_CONNECTIONS_PER_ROUTE = 20;
    private static final int HTTP_MAX_CONNECTIONS_TOTAL = 120;
    private static final int VALIDATOR_HTTP_SOCKET_TIMEOUT = 2000;
    private static final int VALIDATOR_HTTP_MAX_CONNECTIONS_PER_ROUTE = 5;
    private static final int VALIDATOR_HTTP_MAX_CONNECTIONS_TOTAL = 5;

    @Bean
    @ConditionalOnMissingBean(AuthenticationInformationRetriever.class)
    public AuthenticationInformationRetriever<UsernamePasswordAuthenticationToken, CommunityPrincipal> authenticationInformationRetriever() {
        return new SpringSecurityAuthenticationInformationRetriever<>(UsernamePasswordAuthenticationToken.class, CommunityPrincipal.class);
    }

    @Bean
    @ConditionalOnMissingBean(IdolAnnotationsProcessorFactory.class)
    public IdolAnnotationsProcessorFactory annotationsProcessorFactory() {
        return new IdolAnnotationsProcessorFactoryImpl();
    }


    @Bean
    @ConditionalOnMissingBean(name = CONTENT_ACI_SERVICE_BEAN_NAME)
    public AciService contentAciService(@Qualifier(ACI_SERVICE_BEAN_NAME)
                                        final AciService aciService,
                                        final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                return configService.getConfig().getContentAciServerDetails();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = QMS_ACI_SERVICE_BEAN_NAME)
    public AciService qmsAciService(@Qualifier(ACI_SERVICE_BEAN_NAME)
                                    final AciService aciService,
                                    final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                final QueryManipulation queryManipulation = configService.getConfig().getQueryManipulation();
                return queryManipulation != null ? queryManipulation.getServer().toAciServerDetails() : null;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = VIEW_ACI_SERVICE_BEAN_NAME)
    public AciService viewAciService(@Qualifier(ACI_SERVICE_BEAN_NAME)
                                     final AciService aciService,
                                     final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                return configService.getConfig().getViewConfig().toAciServerDetails();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = ANSWER_SERVER_ACI_SERVICE_BEAN_NAME)
    public AciService answerServerAciService(@Qualifier(ACI_SERVICE_BEAN_NAME)
                                           final AciService aciService,
                                           final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                final AnswerServerConfig answerServerConfig = configService.getConfig().getAnswerServer();
                if (answerServerConfig == null || BooleanUtils.isFalse(answerServerConfig.getEnabled())) {
                    throw new IllegalStateException("Attempted to contact AnswerServer but AnswerServer is not configured");
                }

                return answerServerConfig.toAciServerDetails();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = ACI_SERVICE_BEAN_NAME)
    public AciService aciService(final AciHttpClient aciHttpClient) {
        return new AciServiceImpl(aciHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean(name = VALIDATOR_ACI_SERVICE_BEAN_NAME)
    public AciService validatorAciService(final AciHttpClient validatorAciHttpClient) {
        return new AciServiceImpl(validatorAciHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean(name = ACI_HTTP_CLIENT_BEAN_NAME)
    public AciHttpClient aciHttpClient(final HttpClient httpClient) {
        return new AciHttpClientImpl(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean(name = VALIDATOR_ACI_HTTP_CLIENT_BEAN_NAME)
    public AciHttpClient validatorAciHttpClient(final HttpClient validatorHttpClient) {
        return new AciHttpClientImpl(validatorHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean(name = HTTP_CLIENT_BEAN_NAME)
    public HttpClient httpClient() {
        return createHttpClient(HTTP_SOCKET_TIMEOUT, HTTP_MAX_CONNECTIONS_PER_ROUTE, HTTP_MAX_CONNECTIONS_TOTAL);
    }

    @Bean
    @ConditionalOnMissingBean(name = VALIDATOR_HTTP_CLIENT_BEAN_NAME)
    public HttpClient validatorHttpClient() {
        return createHttpClient(VALIDATOR_HTTP_SOCKET_TIMEOUT, VALIDATOR_HTTP_MAX_CONNECTIONS_PER_ROUTE, VALIDATOR_HTTP_MAX_CONNECTIONS_TOTAL);
    }

    private HttpClient createHttpClient(final int httpSocketTimeout, final int maxConnectionsPerRoute, final int maxConnectionsTotal) {
        final SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(httpSocketTimeout)
                .build();

        return HttpClientBuilder.create()
                .setMaxConnPerRoute(maxConnectionsPerRoute)
                .setMaxConnTotal(maxConnectionsTotal)
                .setDefaultSocketConfig(socketConfig)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(IdolComponentLabelLookup.class)
    public IdolComponentLabelLookup createIdolComponentLabelLookup (
            final ConfigService<? extends IdolSearchCapable> configService
    ) {
        return (host, port) -> configService.getConfig().lookupComponentNameByHostAndPort(host, port);
    }
}
