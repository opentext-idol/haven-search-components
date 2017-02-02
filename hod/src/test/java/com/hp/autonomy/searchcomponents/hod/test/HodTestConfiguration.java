/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.Authentication;
import com.hp.autonomy.frontend.configuration.authentication.AuthenticationConfig;
import com.hp.autonomy.frontend.configuration.authentication.BCryptUsernameAndPassword;
import com.hp.autonomy.frontend.configuration.authentication.SingleUserAuthentication;
import com.hp.autonomy.hod.client.api.authentication.ApiKey;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationService;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationServiceImpl;
import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.Resource;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.config.HodServiceConfig;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.InMemoryTokenRepository;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.client.token.TokenProxyService;
import com.hp.autonomy.hod.client.token.TokenRepository;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.hod.sso.HodSsoConfig;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.configuration.QueryManipulationConfig;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@Configuration
@ConditionalOnProperty(value = HodTestConfiguration.MOCK_CONFIGURATION_PROPERTY, matchIfMissing = true)
public class HodTestConfiguration {
    public static final String MOCK_CONFIGURATION_PROPERTY = "mock.configuration";
    public static final String MOCK_AUTHENTICATION_PROPERTY = "mock.authentication";
    public static final String MOCK_AUTHENTICATION_RETRIEVER_PROPERTY = "mock.authenticationRetriever";

    public static final String HOD_URL = "https://api.havenondemand.com";
    public static final String PROXY_HOST = "web-proxy.sdc.hpecorp.net";
    public static final int PROXY_PORT = 8080;

    public static final String QUERY_PROFILE = "search_default_profile";
    public static final String QUERY_MANIPULATION_INDEX = "search_default_index";
    public static final Set<String> ALLOWED_ORIGINS = Collections.singleton("http://localhost:8080");

    public static final String API_KEY_PROPERTY = "test.api.key";
    public static final String APPLICATION_PROPERTY = "test.application";
    public static final String DOMAIN_PROPERTY = "test.domain";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    public ConfigService<HodSearchCapable> configService() {
        @SuppressWarnings("unchecked") final ConfigService<HodSearchCapable> configService = (ConfigService<HodSearchCapable>) mock(ConfigService.class);

        final HodSearchCapable config = mock(HodSearchCapable.class);

        when(config.getQueryManipulation()).thenReturn(QueryManipulationConfig.builder().profile(QUERY_PROFILE).index(QUERY_MANIPULATION_INDEX).build());
        when(config.getFieldsInfo()).thenReturn(FieldsInfo.builder().build());

        when(configService.getConfig()).thenReturn(config);

        return configService;
    }

    @Bean
    @Primary
    public ConfigService<HodSsoConfig> hodSsoConfigService() {
        @SuppressWarnings("unchecked") final ConfigService<HodSsoConfig> configService = (ConfigService<HodSsoConfig>) mock(ConfigService.class);

        final HodSsoConfig config = mock(HodSsoConfig.class);

        when(config.getApiKey()).thenReturn(new ApiKey(environment.getProperty(API_KEY_PROPERTY)));
        when(config.getAllowedOrigins()).thenReturn(ALLOWED_ORIGINS);

        when(configService.getConfig()).thenReturn(config);

        return configService;
    }

    @Bean
    @Primary
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ConfigService<AuthenticationConfig<?>> authenticationConfigService() {
        final ConfigService<AuthenticationConfig<?>> configService = (ConfigService<AuthenticationConfig<?>>) mock(ConfigService.class);

        final AuthenticationConfig<?> config = mock(AuthenticationConfig.class);
        final SingleUserAuthentication login = SingleUserAuthentication.builder()
                .method("singleUser")
                .singleUser(BCryptUsernameAndPassword.builder()
                        .username("admin")
                        .build())
                .build();

        when(config.getAuthentication()).thenReturn((Authentication) login);
        when(configService.getConfig()).thenReturn((AuthenticationConfig) config);

        return configService;
    }


    @Bean
    @ConditionalOnProperty(value = MOCK_AUTHENTICATION_PROPERTY, matchIfMissing = true)
    public TokenProxy<EntityType.Application, TokenType.Simple> testTokenProxy(
            final HttpClient httpClient,
            final TokenRepository tokenRepository,
            final ObjectMapper objectMapper
    ) throws HodErrorException {
        final String application = environment.getProperty(APPLICATION_PROPERTY);
        final String domain = environment.getProperty(DOMAIN_PROPERTY);
        final ApiKey apiKey = new ApiKey(environment.getProperty(API_KEY_PROPERTY));

        // We can't use the HodServiceConfig bean here since the AuthenticationInformationRetrieverTokenProxyService depends on this bean
        final HodServiceConfig<?, TokenType.Simple> hodServiceConfig = new HodServiceConfig.Builder<EntityType.Combined, TokenType.Simple>(HOD_URL)
                .setHttpClient(httpClient)
                .setTokenRepository(tokenRepository)
                .setObjectMapper(objectMapper)
                .build();

        final AuthenticationService authenticationService = new AuthenticationServiceImpl(hodServiceConfig);
        return authenticationService.authenticateApplication(apiKey, application, domain, TokenType.Simple.INSTANCE);
    }

    @Bean
    @ConditionalOnProperty(value = MOCK_AUTHENTICATION_PROPERTY, matchIfMissing = true)
    public HodAuthenticationPrincipal testPrincipal() {
        final String application = environment.getProperty(APPLICATION_PROPERTY);
        final String domain = environment.getProperty(DOMAIN_PROPERTY);
        final String userStoreName = "DEFAULT_USER_STORE";

        final Resource userStore = Resource.builder()
                .uuid(UUID.randomUUID())
                .domain(domain)
                .name(userStoreName)
                .build();

        final HodAuthenticationPrincipal testPrincipal = mock(HodAuthenticationPrincipal.class);
        when(testPrincipal.getApplication()).thenReturn(new ResourceName(domain, application));
        when(testPrincipal.getUserUuid()).thenReturn(UUID.randomUUID());
        when(testPrincipal.getUserStoreInformation()).thenReturn(userStore);
        when(testPrincipal.getTenantUuid()).thenReturn(UUID.randomUUID());
        when(testPrincipal.getUserMetadata()).thenReturn(Collections.emptyMap());
        return testPrincipal;
    }

    @Bean
    @ConditionalOnMissingBean(HodServiceConfig.class)
    public HodServiceConfig<EntityType.Combined, TokenType.Simple> hodServiceConfig(
            final TokenProxyService<EntityType.Combined, TokenType.Simple> tokenProxyService,
            final HttpClient httpClient,
            final TokenRepository tokenRepository,
            final ObjectMapper objectMapper
    ) {
        return new HodServiceConfig.Builder<EntityType.Combined, TokenType.Simple>(HOD_URL)
                .setTokenProxyService(tokenProxyService)
                .setHttpClient(httpClient)
                .setTokenRepository(tokenRepository)
                .setObjectMapper(objectMapper)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(HttpClient.class)
    public HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setProxy(new HttpHost(PROXY_HOST, PROXY_PORT))
                .disableCookieManagement()
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(TokenRepository.class)
    public TokenRepository tokenRepository() {
        return new InMemoryTokenRepository();
    }
}
