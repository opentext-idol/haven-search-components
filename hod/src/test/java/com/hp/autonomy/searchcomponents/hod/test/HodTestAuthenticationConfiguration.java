package com.hp.autonomy.searchcomponents.hod.test;

import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConditionalOnProperty(value = "mock.configuration", matchIfMissing = true)
public class HodTestAuthenticationConfiguration {
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
