package com.hp.autonomy.searchcomponents.hod.authentication;

import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.client.token.TokenProxyService;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationInformationRetrieverTokenProxyService<E extends EntityType> implements TokenProxyService<E, TokenType.Simple> {
    final AuthenticationInformationRetriever<HodAuthentication<E>, HodAuthenticationPrincipal> authenticationRetriever;

    @Autowired
    public AuthenticationInformationRetrieverTokenProxyService(final AuthenticationInformationRetriever<HodAuthentication<E>, HodAuthenticationPrincipal> authenticationRetriever) {
        this.authenticationRetriever = authenticationRetriever;
    }

    @Override
    public TokenProxy<E, TokenType.Simple> getTokenProxy() {
        final HodAuthentication<E> authentication = authenticationRetriever.getAuthentication();
        return authentication.getTokenProxy();
    }
}
