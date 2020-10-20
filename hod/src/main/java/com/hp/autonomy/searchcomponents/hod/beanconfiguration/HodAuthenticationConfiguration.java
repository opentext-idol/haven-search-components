/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
