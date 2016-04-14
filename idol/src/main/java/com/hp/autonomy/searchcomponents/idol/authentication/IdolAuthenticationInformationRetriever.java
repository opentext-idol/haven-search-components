/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.authentication;

import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class IdolAuthenticationInformationRetriever implements AuthenticationInformationRetriever<CommunityPrincipal> {
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public CommunityPrincipal getPrincipal() {
        final Authentication authentication = getAuthentication();
        return authentication != null ? (CommunityPrincipal) authentication.getPrincipal() : null;
    }
}
