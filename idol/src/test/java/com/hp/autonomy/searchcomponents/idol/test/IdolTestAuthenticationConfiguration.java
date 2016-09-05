package com.hp.autonomy.searchcomponents.idol.test;

import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConditionalOnProperty(value = "mock.configuration", matchIfMissing = true)
public class IdolTestAuthenticationConfiguration {
    @Bean
    @Primary
    @ConditionalOnProperty(value = "mock.authenticationRetriever", matchIfMissing = true)
    public AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever() {
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        final CommunityPrincipal communityPrincipal = mock(CommunityPrincipal.class);
        when(communityPrincipal.getId()).thenReturn(1L);
        when(communityPrincipal.getUsername()).thenReturn("user");
        when(authentication.getPrincipal()).thenReturn(communityPrincipal);

        @SuppressWarnings("unchecked")
        final AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever = mock(AuthenticationInformationRetriever.class);
        when(authenticationInformationRetriever.getAuthentication()).thenReturn(authentication);
        when(authenticationInformationRetriever.getPrincipal()).thenReturn(communityPrincipal);
        return authenticationInformationRetriever;
    }
}
