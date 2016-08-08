package com.hp.autonomy.searchcomponents.core.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class SpringSecurityAuthenticationInformationRetriever<A extends Authentication, P extends Principal> implements AuthenticationInformationRetriever<A, P> {
    @Override
    public A getAuthentication() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //noinspection unchecked
        return (A) authentication;
    }

    @Override
    public P getPrincipal() {
        final A authentication = getAuthentication();

        //noinspection unchecked
        return authentication == null ? null : (P) authentication.getPrincipal();
    }
}
