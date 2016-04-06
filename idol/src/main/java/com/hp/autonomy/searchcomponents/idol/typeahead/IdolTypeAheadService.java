/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("typeAheadService")
public class IdolTypeAheadService implements TypeAheadService<AciErrorException> {
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final TypeAheadService<AciErrorException> termExpandService;
    private final TypeAheadService<AciErrorException> qmsService;

    @Autowired
    public IdolTypeAheadService(
            final ConfigService<? extends IdolSearchCapable> configService,
            final TypeAheadService<AciErrorException> termExpandTypeAheadService,
            final TypeAheadService<AciErrorException> qmsTypeAheadService
    ) {
        this.configService = configService;
        termExpandService = termExpandTypeAheadService;
        qmsService = qmsTypeAheadService;
    }

    @Override
    public List<String> getSuggestions(final String text) {
        return configService.getConfig().getQueryManipulation().isEnabled() ? qmsService.getSuggestions(text) : termExpandService.getSuggestions(text);
    }
}
