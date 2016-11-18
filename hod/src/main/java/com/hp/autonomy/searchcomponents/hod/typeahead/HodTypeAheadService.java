/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.typeahead;

import com.hp.autonomy.hod.caching.CachingConfiguration;
import com.hp.autonomy.hod.client.api.analysis.autocomplete.AutocompleteService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService.TYPE_AHEAD_SERVICE_BEAN_NAME;

/**
 * Default HoD implementation of {@link TypeAheadService}
 */
@Service(TYPE_AHEAD_SERVICE_BEAN_NAME)
class HodTypeAheadService implements TypeAheadService<HodErrorException> {
    private final AutocompleteService autocompleteService;

    @Autowired
    HodTypeAheadService(final AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.TYPE_AHEAD, cacheResolver = CachingConfiguration.SIMPLE_CACHE_RESOLVER_NAME)
    public List<String> getSuggestions(final String text) throws HodErrorException {
        return StringUtils.isBlank(text) ? Collections.emptyList() : autocompleteService.getSuggestions(text);
    }
}
