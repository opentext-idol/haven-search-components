/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService.TYPE_AHEAD_SERVICE_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.typeahead.IdolTypeAheadConstants.QMS_TYPE_AHEAD_SERVICE_BEAN_NAME;
import static com.hp.autonomy.searchcomponents.idol.typeahead.IdolTypeAheadConstants.TERM_EXPAND_TYPE_AHEAD_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link TypeAheadService}
 */
@Service(TYPE_AHEAD_SERVICE_BEAN_NAME)
class IdolTypeAheadService implements TypeAheadService<AciErrorException> {
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final TypeAheadService<AciErrorException> termExpandService;
    private final TypeAheadService<AciErrorException> qmsService;

    @Autowired
    IdolTypeAheadService(
            final ConfigService<? extends IdolSearchCapable> configService,
            @Qualifier(TERM_EXPAND_TYPE_AHEAD_SERVICE_BEAN_NAME)
            final TypeAheadService<AciErrorException> termExpandTypeAheadService,
            @Qualifier(QMS_TYPE_AHEAD_SERVICE_BEAN_NAME)
            final TypeAheadService<AciErrorException> qmsTypeAheadService
    ) {
        this.configService = configService;
        termExpandService = termExpandTypeAheadService;
        qmsService = qmsTypeAheadService;
    }

    @Override
    public List<String> getSuggestions(final String text) {
        return BooleanUtils.isTrue(configService.getConfig().getQueryManipulation().getEnabled()) ? qmsService.getSuggestions(text) : termExpandService.getSuggestions(text);
    }
}
