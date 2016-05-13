/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.typeahead;

import com.hp.autonomy.hod.client.api.analysis.autocomplete.AutocompleteService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HodTypeAheadServiceTest {
    @Mock
    private AutocompleteService autocompleteService;

    private TypeAheadService<HodErrorException> typeAheadService;

    @Before
    public void setUp() {
        typeAheadService = new HodTypeAheadService(autocompleteService);
    }

    @Test
    public void getSuggestions() throws HodErrorException {
        when(autocompleteService.getSuggestions("badge")).thenReturn(Collections.singletonList("badger"));
        assertThat(typeAheadService.getSuggestions("badge"), hasItem(is("badger")));
    }

    @Test
    public void getSuggestionsNoText() throws HodErrorException {
        assertThat(typeAheadService.getSuggestions(null), is(empty()));
    }
}
