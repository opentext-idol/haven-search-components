/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.authentication;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractAuthenticationInformationRetrieverTest<A extends Authentication, P extends Principal> {
    private static SecurityContext existingSecurityContext;

    @BeforeClass
    public static void init() {
        existingSecurityContext = SecurityContextHolder.getContext();
    }

    @AfterClass
    public static void destroy() {
        SecurityContextHolder.setContext(existingSecurityContext);
    }

    @Mock
    private SecurityContext securityContext;

    protected AuthenticationInformationRetriever<P> authenticationInformationRetriever;
    protected A authentication;
    protected P principal;

    @Test
    public void getPrincipal() {
        when(authentication.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertNotNull(authenticationInformationRetriever.getPrincipal());
    }
}
