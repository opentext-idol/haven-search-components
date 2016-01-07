/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.impl.AciServiceImpl;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.transport.impl.AciHttpClientImpl;
import com.hp.autonomy.frontend.configuration.AbstractConfigurableAciService;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.user.UserService;
import com.hp.autonomy.user.UserServiceConfig;
import com.hp.autonomy.user.UserServiceImpl;
import org.apache.http.client.HttpClient;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class HavenSearchIdolConfiguration<C extends HavenSearchCapable & UserServiceConfig> {
    private static final int HTTP_SOCKET_TIMEOUT = 90000;
    private static final int HTTP_MAX_CONNECTIONS_PER_ROUTE = 20;
    private static final int HTTP_MAX_CONNECTIONS_TOTAL = 120;

    @Bean
    AciService contentAciService(@Qualifier("aciService") final AciService aciService, final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                return configService.getConfig().getContent().toAciServerDetails();
            }
        };
    }

    @Bean
    AciService viewAciService(@Qualifier("aciService") final AciService aciService, final ConfigService<C> configService) {
        return new AbstractConfigurableAciService(aciService) {
            @Override
            public AciServerDetails getServerDetails() {
                return configService.getConfig().getViewConfig().toAciServerDetails();
            }
        };
    }

    @Bean
    public UserService userService(final ConfigService<C> configService, final AciService aciService, final AciResponseJaxbProcessorFactory processorFactory) {
        return new UserServiceImpl(configService, aciService, new IdolAnnotationsProcessorFactoryImpl());
    }

    @Bean
    public AciResponseJaxbProcessorFactory processorFactory() {
        return new AciResponseJaxbProcessorFactory();
    }

    @Bean
    public AciService aciService() {
        return new AciServiceImpl(new AciHttpClientImpl(httpClient()));
    }

    @Bean
    public HttpClient httpClient() {
        final SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(HTTP_SOCKET_TIMEOUT)
                .build();

        return HttpClientBuilder.create()
                .setMaxConnPerRoute(HTTP_MAX_CONNECTIONS_PER_ROUTE)
                .setMaxConnTotal(HTTP_MAX_CONNECTIONS_TOTAL)
                .setDefaultSocketConfig(socketConfig)
                .build();
    }
}
