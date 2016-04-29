/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.beanconfiguration;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.authentication.SpringSecurityAuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.IdolDocumentService;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.searchcomponents.idol.search.QueryResponseParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
class DefaultIdolConfiguration {
    @SuppressWarnings("MethodWithTooManyParameters")
    @Bean
    @ConditionalOnMissingBean(DocumentsService.class)
    public DocumentsService<String, IdolSearchResult, AciErrorException> documentsService(final ConfigService<? extends IdolSearchCapable> configService, final HavenSearchAciParameterHandler parameterHandler, final QueryResponseParser queryResponseParser, final AciService contentAciService, final AciService qmsAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        return new IdolDocumentService(configService, parameterHandler, queryResponseParser, contentAciService, qmsAciService, aciResponseProcessorFactory);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationInformationRetriever.class)
    public AuthenticationInformationRetriever<UsernamePasswordAuthenticationToken, CommunityPrincipal> authenticationInformationRetriever() {
        return new SpringSecurityAuthenticationInformationRetriever<>();
    }
}
