/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.beanconfiguration;

import com.hp.autonomy.hod.client.api.authentication.EntityType;
import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import com.hpe.bigdata.frontend.spring.authentication.SpringSecurityAuthenticationInformationRetriever;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HodAuthenticationConfiguration {
    // Note that this bean cannot go in HavenSearchHodApplication because it would then cause a circular dependency in upstream applications
    @Bean
    @ConditionalOnMissingBean(AuthenticationInformationRetriever.class)
    public AuthenticationInformationRetriever<HodAuthentication<EntityType.Combined>, HodAuthenticationPrincipal> authenticationInformationRetriever() {
        @SuppressWarnings("unchecked")
        final AuthenticationInformationRetriever<HodAuthentication<EntityType.Combined>, HodAuthenticationPrincipal> retriever =
                new SpringSecurityAuthenticationInformationRetriever<>((Class<HodAuthentication<EntityType.Combined>>) (Class<?>) HodAuthentication.class, HodAuthenticationPrincipal.class);
        return retriever;
    }
}
