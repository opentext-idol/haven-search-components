package com.hp.autonomy.searchcomponents.hod.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.hod.client.api.authentication.ApiKey;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationService;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationServiceImpl;
import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.authentication.tokeninformation.UserStoreInformation;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.config.HodServiceConfig;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.client.token.TokenRepository;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class HodTestAuthenticationConfiguration {
    public static final String APPLICATION_PROPERTY = "test.application";
    public static final String DOMAIN_PROPERTY = "test.domain";

    @Autowired
    private Environment environment;

    @Bean
    public TokenProxy<EntityType.Application, TokenType.Simple> testTokenProxy(
            final HttpClient httpClient,
            final TokenRepository tokenRepository,
            final ObjectMapper hodSearchResultObjectMapper
    ) throws HodErrorException {
        final String application = environment.getProperty(APPLICATION_PROPERTY);
        final String domain = environment.getProperty(DOMAIN_PROPERTY);
        final ApiKey apiKey = new ApiKey(environment.getProperty(HodTestConfiguration.API_KEY_PROPERTY));

        // We can't use the HodServiceConfig bean here since the AuthenticationInformationRetrieverTokenProxyService depends on this bean
        final HodServiceConfig<?, TokenType.Simple> hodServiceConfig = new HodServiceConfig.Builder<EntityType.Combined, TokenType.Simple>(HodTestConfiguration.HOD_URL)
                .setHttpClient(httpClient)
                .setTokenRepository(tokenRepository)
                .setObjectMapper(hodSearchResultObjectMapper)
                .build();

        final AuthenticationService authenticationService = new AuthenticationServiceImpl(hodServiceConfig);
        return authenticationService.authenticateApplication(apiKey, application, domain, TokenType.Simple.INSTANCE);
    }

    @Bean
    public HodAuthenticationPrincipal testPrincipal() {
        final String application = environment.getProperty(APPLICATION_PROPERTY);
        final String domain = environment.getProperty(DOMAIN_PROPERTY);
        final String userStoreName = "DEFAULT_USER_STORE";

        final UserStoreInformation userStoreInformation = mock(UserStoreInformation.class);
        when(userStoreInformation.getDomain()).thenReturn(domain);
        when(userStoreInformation.getUuid()).thenReturn(UUID.randomUUID());
        when(userStoreInformation.getName()).thenReturn(userStoreName);
        when(userStoreInformation.getIdentifier()).thenReturn(new ResourceIdentifier(domain, userStoreName));

        final HodAuthenticationPrincipal testPrincipal = mock(HodAuthenticationPrincipal.class);
        when(testPrincipal.getApplication()).thenReturn(new ResourceIdentifier(domain, application));
        when(testPrincipal.getUserUuid()).thenReturn(UUID.randomUUID());
        when(testPrincipal.getUserStoreInformation()).thenReturn(userStoreInformation);
        when(testPrincipal.getTenantUuid()).thenReturn(UUID.randomUUID());
        when(testPrincipal.getUserMetadata()).thenReturn(Collections.<String, Serializable>emptyMap());
        return testPrincipal;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(value = "mock.authenticationRetriever", matchIfMissing = true)
    public AuthenticationInformationRetriever<HodAuthentication<EntityType.Application>, HodAuthenticationPrincipal> authenticationInformationRetriever(
            final HodAuthenticationPrincipal testPrincipal,
            final TokenProxy<EntityType.Application, TokenType.Simple> testTokenProxy
    ) throws HodErrorException {
        @SuppressWarnings("unchecked")
        final HodAuthentication<EntityType.Application> mockAuthentication = mock(HodAuthentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(testPrincipal);
        when(mockAuthentication.getTokenProxy()).thenReturn(testTokenProxy);

        @SuppressWarnings("unchecked")
        final AuthenticationInformationRetriever<HodAuthentication<EntityType.Application>, HodAuthenticationPrincipal> retriever = mock(AuthenticationInformationRetriever.class);

        when(retriever.getAuthentication()).thenReturn(mockAuthentication);
        when(retriever.getPrincipal()).thenReturn(testPrincipal);

        return retriever;
    }
}
