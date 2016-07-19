/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AciServiceRetrieverImpl implements AciServiceRetriever {
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final AciService contentAciService;
    private final AciService qmsAciService;

    @Autowired
    public AciServiceRetrieverImpl(final ConfigService<? extends IdolSearchCapable> configService, final AciService contentAciService, final AciService qmsAciService) {
        this.configService = configService;
        this.contentAciService = contentAciService;
        this.qmsAciService = qmsAciService;
    }

    @Override
    public boolean qmsEnabled() {
        return configService.getConfig().getQueryManipulation() != null && configService.getConfig().getQueryManipulation().isEnabled();
    }

    @Override
    public AciService getAciService(final SearchRequest.QueryType queryType) {
        final boolean useQms = qmsEnabled() && queryType != SearchRequest.QueryType.RAW;
        return useQms ? qmsAciService : contentAciService;
    }
}
