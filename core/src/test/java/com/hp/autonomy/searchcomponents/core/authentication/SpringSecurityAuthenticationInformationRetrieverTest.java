/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.authentication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpringSecurityAuthenticationInformationRetrieverTest {
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Principal principal;

    private SecurityContext existingSecurityContext;
    private AuthenticationInformationRetriever<Authentication, Principal> informationRetriever;

    @Before
    public void initialise() {
        existingSecurityContext = SecurityContextHolder.getContext();

        when(authentication.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        informationRetriever = new SpringSecurityAuthenticationInformationRetriever<>();
    }

    @After
    public void destroy() {
        SecurityContextHolder.setContext(existingSecurityContext);
    }

    @Test
    public void getAuthentication() {
        assertThat(informationRetriever.getAuthentication(), is(authentication));
    }

    @Test
    public void getPrincipal() {
        assertNotNull(informationRetriever.getPrincipal());
    }
}
