/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.impl.AciServiceImpl;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.transport.impl.AciHttpClientImpl;
import com.hp.autonomy.frontend.configuration.AbstractConfigurableAciService;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import org.apache.http.client.HttpClient;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Defines Spring beans required for using this module
 *
 * @param <C> application config type
 */
@Configuration
@ComponentScan("com.hp.autonomy.searchcomponents.idol")
public class HavenSearchIdolConfiguration<C extends IdolSearchCapable> {
    private static final int HTTP_SOCKET_TIMEOUT = 90000;
    private static final int HTTP_MAX_CONNECTIONS_PER_ROUTE = 20;
    private static final int HTTP_MAX_CONNECTIONS_TOTAL = 120;
    private static final int VALIDATOR_HTTP_SOCKET_TIMEOUT = 2000;
    private static final int VALIDATOR_HTTP_MAX_CONNECTIONS_PER_ROUTE = 5;
    private static final int VALIDATOR_HTTP_MAX_CONNECTIONS_TOTAL = 5;

    @Bean
    public AciService contentAciService(@Qualifier("aciService") final AciService aciService, final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                return configService.getConfig().getContentAciServerDetails();
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean(name = "qmsAciService")
    public AciService qmsAciService(@Qualifier("aciService") final AciService aciService, final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                final QueryManipulation queryManipulation = configService.getConfig().getQueryManipulation();
                return queryManipulation != null ? queryManipulation.getServer().toAciServerDetails() : null;
            }
        };
    }

    @Bean
    public AciService viewAciService(@Qualifier("aciService") final AciService aciService, final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                return configService.getConfig().getViewConfig().toAciServerDetails();
            }
        };
    }

    @Bean
    public IdolAnnotationsProcessorFactory annotationsProcessorFactory() {
        return new IdolAnnotationsProcessorFactoryImpl();
    }

    @Bean
    public AciResponseJaxbProcessorFactory processorFactory() {
        return new AciResponseJaxbProcessorFactory();
    }

    @Bean
    public AciService aciService(final HttpClient httpClient) {
        return new AciServiceImpl(new AciHttpClientImpl(httpClient));
    }

    @Bean
    public AciService validatorAciService(final HttpClient validatorHttpClient) {
        return new AciServiceImpl(new AciHttpClientImpl(validatorHttpClient));
    }

    @Bean
    public HttpClient httpClient() {
        return createHttpClient(HTTP_SOCKET_TIMEOUT, HTTP_MAX_CONNECTIONS_PER_ROUTE, HTTP_MAX_CONNECTIONS_TOTAL);
    }

    @Bean
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
}
