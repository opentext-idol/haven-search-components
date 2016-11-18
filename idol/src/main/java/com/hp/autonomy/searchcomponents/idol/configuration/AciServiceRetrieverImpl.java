/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link AciServiceRetriever}
 */
@Component(AciServiceRetriever.ACI_SERVICE_RETRIEVER_BEAN_NAME)
class AciServiceRetrieverImpl implements AciServiceRetriever {
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final AciService contentAciService;
    private final AciService qmsAciService;

    @Autowired
    AciServiceRetrieverImpl(final ConfigService<? extends IdolSearchCapable> configService, final AciService contentAciService, final AciService qmsAciService) {
        this.configService = configService;
        this.contentAciService = contentAciService;
        this.qmsAciService = qmsAciService;
    }

    @Override
    public boolean qmsEnabled() {
        return configService.getConfig().getQueryManipulation() != null && BooleanUtils.isTrue(configService.getConfig().getQueryManipulation().getEnabled());
    }

    @Override
    public AciService getAciService(final QueryRequest.QueryType queryType) {
        final boolean useQms = qmsEnabled() && queryType != QueryRequest.QueryType.RAW;
        return useQms ? qmsAciService : contentAciService;
    }
}
