package com.hp.autonomy.searchcomponents.idol.test;

import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.mockito.Mockito.mock;

@Configuration
@ConditionalOnProperty(value = "mock.configuration", matchIfMissing = true)
public class IdolTestAuthenticationConfiguration {
    @Bean
    @Primary
    @ConditionalOnProperty(value = "mock.authenticationRetriever", matchIfMissing = true)
    public AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever() {
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);

        final CommunityPrincipal communityPrincipal = mock(CommunityPrincipal.class);
        Mockito.lenient().when(communityPrincipal.getId()).thenReturn(1L);
        Mockito.lenient().when(communityPrincipal.getUsername()).thenReturn("user");
        Mockito.lenient().when(communityPrincipal.getName()).thenReturn("user");
        Mockito.lenient().when(authentication.getPrincipal()).thenReturn(communityPrincipal);

        @SuppressWarnings("unchecked")
        final AuthenticationInformationRetriever<UsernamePasswordAuthenticationToken, CommunityPrincipal> authenticationInformationRetriever = mock(AuthenticationInformationRetriever.class);
        Mockito.lenient().when(authenticationInformationRetriever.getAuthentication()).thenReturn(authentication);
        Mockito.lenient().when(authenticationInformationRetriever.getPrincipal()).thenReturn(communityPrincipal);
        return authenticationInformationRetriever;
    }
}
